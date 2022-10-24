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
import ru.meatgames.tomb.screen.compose.game.MapTile
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

                    val characterScreenX = viewportWidth / 2
                    val characterScreenY = viewportHeight / 2

                    val mapOffsetX = character.mapX - characterScreenX
                    val mapOffsetY = character.mapY - characterScreenY

                    val filteredTiles = filterTiles(
                        streamedTiles = tiles,
                        mapX = mapOffsetX,
                        mapY = mapOffsetY,
                        mapWidth = map.mapWrapper.width,
                        mapHeight = map.mapWrapper.height,
                    )

                    val mask = BooleanArray(filteredTiles.size) { false }

                    computeFov(
                        originX = characterScreenX,
                        originY = characterScreenY,
                        maxDepth = viewportWidth / 2 + 1,
                        revealTile = { x, y -> mask[x + y * viewportWidth] = true },
                        checkIfTileIsBlocking = { x, y ->
                            !filteredTiles[x + y * viewportWidth].isTransparent
                        }
                    )

                    return@combine MapScreenState.Ready(
                        viewportWidth = viewportWidth,
                        viewportHeight = viewportHeight,
                        tiles = filteredTiles,
                        visibilityMask = mask,
                    )
                }
            }.collect(_state::emit)
        }
    }

    private fun filterTiles(
        streamedTiles: List<MapTile>,
        mapX: Int,
        mapY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): List<MapTile> {
        val tiles = (0 until viewportHeight).map { line ->
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
        }.fold(emptyList<MapTile>()) { acc, item -> acc + item }

        return tiles

        /*return MapScreenState.Ready(
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tiles = tiles,
        )*/
    }

    sealed class MapScreenState {

        data class Ready(
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tiles: List<MapTile>,
            val visibilityMask: BooleanArray,
        ) : MapScreenState() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Ready

                if (viewportWidth != other.viewportWidth) return false
                if (viewportHeight != other.viewportHeight) return false
                if (tiles != other.tiles) return false
                if (!visibilityMask.contentEquals(other.visibilityMask)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = viewportWidth
                result = 31 * result + viewportHeight
                result = 31 * result + tiles.hashCode()
                result = 31 * result + visibilityMask.contentHashCode()
                return result
            }
        }

        object Loading : MapScreenState()

    }

}
