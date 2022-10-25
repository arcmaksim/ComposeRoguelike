package ru.meatgames.tomb.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.di.MAP_VIEWPORT_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_VIEWPORT_WIDTH_KEY
import ru.meatgames.tomb.model.tile.domain.GeneralTilePurpose
import ru.meatgames.tomb.model.tile.domain.Tile
import ru.meatgames.tomb.model.tile.domain.TilePurposeDefinition
import ru.meatgames.tomb.model.tile.domain.isEmpty
import ru.meatgames.tomb.screen.compose.game.MapTile
import ru.meatgames.tomb.screen.compose.game.resolveTileset
import javax.inject.Inject
import javax.inject.Named

class MapScreenController @Inject constructor(
    @Named(MAP_VIEWPORT_WIDTH_KEY)
    private val viewportWidth: Int,
    @Named(MAP_VIEWPORT_HEIGHT_KEY)
    private val viewportHeight: Int,
    private val mapController: MapController,
    private val characterController: CharacterController,
) {

    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state

    private var cachedVisibilityMask = BooleanArray(0) { false }

    private val purposeDefinitionOffsetCache = HashMap<TilePurposeDefinition, IntOffset>()

    init {
        GlobalScope.launch {
            mapController.mapFlow.flatMapLatest { map ->
                if (map !is State.MapAvailable) {
                    return@flatMapLatest flow { MapScreenState.Loading }
                }

                map.mapWrapper.state.combine(
                    characterController.characterStateFlow
                ) { tiles, character ->
                    if (character.mapX == -1 && character.mapY == -1) {
                        return@combine MapScreenState.Loading
                    }

                    return@combine processNewTiles(
                        characterX = character.mapX,
                        characterY = character.mapY,
                        mapWidth = map.mapWrapper.width,
                        mapHeight = map.mapWrapper.height,
                        tiles = tiles,
                    )
                }
            }.collect(_state::emit)
        }
    }

    private fun processNewTiles(
        tiles: List<MapTile>,
        characterX: Int,
        characterY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): MapScreenState.Ready {
        updateVisibilityMask(tiles)

        val characterScreenX = viewportWidth / 2
        val characterScreenY = viewportHeight / 2

        val mapOffsetX = characterX - characterScreenX
        val mapOffsetY = characterY - characterScreenY

        val filteredTiles = filterTiles(
            streamedTiles = tiles,
            mapX = mapOffsetX,
            mapY = mapOffsetY,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
        )

        computeFov(
            originX = characterScreenX,
            originY = characterScreenY,
            maxDepth = viewportWidth / 2 + 1,
            revealTile = { x, y -> cachedVisibilityMask[x + y * viewportWidth] = true },
            checkIfTileIsBlocking = { x, y ->
                !filteredTiles[x + y * viewportWidth].isTransparent
            }
        )

        return produceReadyState(
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tiles = filteredTiles,
        )
    }

    private fun updateVisibilityMask(
        tiles: List<MapTile>,
    ) {
        if (cachedVisibilityMask.size != tiles.size) {
            cachedVisibilityMask = BooleanArray(tiles.size) { false }
        } else {
            cachedVisibilityMask.fill(false)
        }
    }

    private fun filterTiles(
        streamedTiles: List<MapTile>,
        mapX: Int,
        mapY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): List<MapTile> = (0 until viewportHeight).map { line ->
        val start = (mapY + line) * mapWidth + mapX
        val end = start + viewportWidth
        when {
            mapY + line !in 0 until mapHeight -> {
                List(viewportWidth) { MapTile.voidMapTile }
            }

            mapX < 0 -> {
                List(viewportWidth) {
                    when {
                        mapX + it < 0 -> MapTile.voidMapTile
                        else -> streamedTiles[start + it]
                    }
                }
            }

            mapX + viewportWidth > mapWidth -> {
                List(
                    viewportWidth
                ) {
                    when {
                        mapX + it < mapWidth -> streamedTiles[start + it]
                        else -> MapTile.voidMapTile
                    }
                }
            }

            else -> streamedTiles.subList(start, end)
        }
    }.fold(emptyList()) { acc, item -> acc + item }

    private fun produceReadyState(
        viewportWidth: Int,
        viewportHeight: Int,
        tiles: List<MapTile>,
    ): MapScreenState.Ready = MapScreenState.Ready(
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
        tiles = tiles.mapIndexed { index, tile ->
            if (!cachedVisibilityMask[index]) return@mapIndexed null

            val floorTile = tile.floor?.let {
                MapScreenTile(
                    asset = it.purposeDefinition.resolveTileset(),
                    srcOffset = it.getOffset(),
                )
            } ?: MapScreenTile(
                asset = NewAssets.tileset,
                srcOffset = IntOffset(
                    x = NewAssets.tileSize.width * 2,
                    y = NewAssets.tileSize.height * 1,
                ),
            )

            val objectTile = tile.`object`?.let {
                if (it.purposeDefinition.isEmpty) {
                    null
                } else {
                    MapScreenTile(
                        asset = it.purposeDefinition.resolveTileset(),
                        srcOffset = it.getOffset(),
                    )
                }
            }

            MapScreenCell(
                floorTile = floorTile,
                objectTile = objectTile,
            )
        },
    )

    private fun Tile.getOffset(): IntOffset = purposeDefinitionOffsetCache.getOrPut(
        purposeDefinition,
    ) {
        val tileSize = NewAssets.tileSize.width
        when (purposeDefinition) {
            is TilePurposeDefinition.Standard -> IntOffset(
                x = purposeDefinition.horizontalTileOffset * tileSize,
                y = theme.verticalTileOffset * tileSize,
            )
            is TilePurposeDefinition.General -> {
                val (offsetX, offsetY) = when (purposeDefinition.purpose) {
                    GeneralTilePurpose.ClosedDoor -> 3 to 0
                    GeneralTilePurpose.OpenDoor -> 1 to 1
                }
                IntOffset(
                    x = offsetX * tileSize,
                    y = offsetY * tileSize,
                )
            }
        }
    }

    sealed class MapScreenState {

        data class Ready(
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tiles: List<MapScreenCell?>,
        ) : MapScreenState()

        object Loading : MapScreenState()

    }

    data class MapScreenTile(
        val asset: ImageBitmap,
        val srcOffset: IntOffset,
    )

    data class MapScreenCell(
        val floorTile: MapScreenTile,
        val objectTile: MapScreenTile?,
    )

}
