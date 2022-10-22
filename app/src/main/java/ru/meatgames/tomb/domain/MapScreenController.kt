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
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
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

                    val mapOffsetX = character.mapX - viewportWidth / 2
                    val mapOffsetY = character.mapY - viewportHeight / 2

                    return@combine filterTiles(
                        streamedTiles = tiles,
                        mapX = mapOffsetX,
                        mapY = mapOffsetY,
                        mapWidth = map.mapWrapper.width,
                        mapHeight = map.mapWrapper.height,
                    )
                }
            }.collect(_state::emit)
        }
    }

    private fun filterTiles(
        streamedTiles: List<ThemedGameMapTile>,
        mapX: Int,
        mapY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): MapScreenState.Ready {
        val tiles = (0 until viewportHeight).map { line ->
            val start = (mapY + line) * mapWidth + mapX
            val end = start + viewportWidth
            when {
                mapY + line !in 0 until mapHeight -> {
                    List(viewportWidth) { ThemedGameMapTile.voidMapTile }
                }

                mapX < 0 -> {
                    List(viewportWidth) {
                        when {
                            mapX + it < 0 -> ThemedGameMapTile.voidMapTile
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
                            else -> ThemedGameMapTile.voidMapTile
                        }
                    }
                }

                else -> streamedTiles.subList(start, end)
            }
        }.fold(emptyList<ThemedGameMapTile>()) { acc, item -> acc + item }

        return MapScreenState.Ready(
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tiles = tiles,
        )
    }

    sealed class MapScreenState {

        data class Ready(
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tiles: List<ThemedGameMapTile>,
        ) : MapScreenState()

        object Loading : MapScreenState()

    }

}
