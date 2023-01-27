package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.di.MAP_VIEWPORT_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_VIEWPORT_WIDTH_KEY
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.render.CharacterIdleAnimationDirection
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.MapTile
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapScreenController @Inject constructor(
    @Named(MAP_VIEWPORT_WIDTH_KEY)
    private val viewportWidth: Int,
    @Named(MAP_VIEWPORT_HEIGHT_KEY)
    private val viewportHeight: Int,
    private val mapController: MapController,
    private val characterController: CharacterController,
    private val tilesController: TilesController,
    private val mapRenderProcessor: MapRenderProcessor,
) {

    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state

    private var previousCharacterIdleAnimationDirection: CharacterIdleAnimationDirection = CharacterIdleAnimationDirection.Left

    private val cachedVisibilityMask = BooleanArray(viewportWidth * viewportWidth) { false }

    private val preProcessingBufferSizeModifier: Int = 1
    private val preProcessingViewportWidth: Int =
        viewportWidth + 2 * preProcessingBufferSizeModifier
    private val preProcessingViewportHeight: Int =
        viewportHeight + 2 * preProcessingBufferSizeModifier

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
                        points = character.points,
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
        points: Int,
    ): MapScreenState.Ready {
        cachedVisibilityMask.updateVisibilityMask()

        val filteredTiles = tiles.filterTiles(
            mapX = characterX - preProcessingViewportWidth / 2,
            mapY = characterY - preProcessingViewportHeight / 2,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
        )

        val characterScreenX = viewportWidth / 2
        val characterScreenY = viewportHeight / 2

        computeFov(
            originX = characterScreenX,
            originY = characterScreenY,
            maxDepth = viewportWidth / 2 + 1,
            revealTile = { x, y -> cachedVisibilityMask[x + y * viewportWidth] = true },
            checkIfTileIsBlocking = { x, y ->
                val index = (x + 1) + (y + 1) * preProcessingViewportWidth
                val objectEntity = filteredTiles[index]?.objectEntityTile ?: return@computeFov false
                !tilesController.isObjectEntityVisibleThrough(
                    objectEntity = objectEntity,
                )
            }
        )

        return MapScreenState.Ready(
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tiles = mapRenderProcessor.produceRenderTilesFrom(
                tiles = filteredTiles,
                tilesLineWidth = preProcessingViewportWidth,
                shouldRenderTile = { index ->
                    cachedVisibilityMask[index]
                },
            ),
            points = points,
        )
    }

    private fun BooleanArray.updateVisibilityMask() {
        fill(false)
    }

    private fun List<MapTile>.filterTiles(
        mapX: Int,
        mapY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): List<MapTile?> = (0 until preProcessingViewportHeight).map { line ->
        val start = (mapY + line) * mapWidth + mapX
        val end = start + preProcessingViewportWidth
        when {
            mapY + line !in 0 until mapHeight -> {
                List(preProcessingViewportWidth) { null }
            }

            mapX < 0 -> {
                List(preProcessingViewportWidth) {
                    when {
                        mapX + it < 0 -> null
                        else -> this[start + it]
                    }
                }
            }

            mapX + preProcessingViewportWidth > mapWidth -> {
                List(preProcessingViewportWidth) {
                    when {
                        mapX + it < mapWidth -> this[start + it]
                        else -> null
                    }
                }
            }

            else -> this.subList(start, end)
        }
    }.fold(emptyList()) { acc, item -> acc + item }

    sealed class MapScreenState {

        data class Ready(
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tiles: List<MapRenderTile?>,
            val points: Int,
        ) : MapScreenState()

        object Loading : MapScreenState()

    }

}
