package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.meatgames.tomb.*
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import ru.meatgames.tomb.domain.map_screen.ThemedGameMapChunk
import javax.inject.Inject

const val themedViewportWidth = 11
const val themedViewportHeight = 11

@HiltViewModel
class ThemedGameScreenViewModel @Inject constructor(
    private val mapGenerator: ThemedMapGenerator,
    private val mapInteractionController: PlayerMapInteractionController,
) : ViewModel() {

    private val _visibleMapChunk = MutableStateFlow(
        ThemedGameMapChunk(
            themedViewportWidth,
            themedViewportHeight,
            mapOffsetX = 0,
            mapOffsetY = 0,
            gameMapTiles = emptyList(),
        )
    )
    val visibleMapChunk: StateFlow<ThemedGameMapChunk> = _visibleMapChunk

    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle

    private val mapController: ThemedMapController

    init {
        val generatedMapConfig = mapGenerator.generateMap()

        _visibleMapChunk.value = ThemedGameMapChunk(
            mapOffsetX = generatedMapConfig.startingPositionX - themedViewportWidth / 2,
            mapOffsetY = generatedMapConfig.startingPositionY - themedViewportHeight / 2,
            gameMapTiles = emptyList(),
        )

        mapController = generatedMapConfig.mapController

        viewModelScope.launch {
            generatedMapConfig.mapController.map.collect { wrapper ->
                _visibleMapChunk.update { previousChunk ->
                    previousChunk.update(wrapper)
                }
            }
        }
    }

    private fun ThemedGameMapChunk.update(
        wrapper: ThemedMapWrapper,
    ): ThemedGameMapChunk = copy(
        gameMapTiles = (0 until themedViewportHeight).map { line ->
            val start = (mapOffsetY + line) * wrapper.height + mapOffsetX
            val end = start + themedViewportWidth
            when {
                mapOffsetY + line !in 0 until wrapper.height -> Array(themedViewportWidth) { ThemedGameMapTile.voidMapTile }
                mapOffsetX < 0 -> Array(themedViewportWidth) {
                    when {
                        mapOffsetX + it < 0 -> ThemedGameMapTile.voidMapTile
                        else -> wrapper.tiles[start + it]
                    }
                }

                mapOffsetX + themedViewportWidth > wrapper.width -> Array(themedViewportWidth) {
                    when {
                        mapOffsetX + it < wrapper.width -> wrapper.tiles[start + it]
                        else -> ThemedGameMapTile.voidMapTile
                    }
                }

                else -> wrapper.tiles.copyOfRange(start, end)
            }
        }.fold(emptyList()) { acc, item -> acc + item },
    )

    fun onMoveCharacter(
        moveDirection: Direction,
    ) {
        if (!isIdle.value) return

        _isIdle.value = false

        val (offsetX, offsetY) = moveDirection.resolvedOffsets

        val chunk = visibleMapChunk.value
        val index = viewportWidth * viewportHeight / 2 + offsetX + offsetY * viewportWidth
        val tile = chunk.gameMapTiles[index]

        when {
            tile.`object`?.isUsable == true -> {
                mapInteractionController.useTile(
                    tile = tile,
                    mapX = chunk.mapOffsetX + viewportWidth / 2 + offsetX,
                    mapY = chunk.mapOffsetY + viewportHeight / 2 + offsetY,
                )
            }

            chunk.gameMapTiles[index].isPassable -> {
                updateVisibleMapChunk(offsetX, offsetY)
            }
        }

        _isIdle.value = true
    }

    private fun updateVisibleMapChunk(
        offsetX: Int,
        offsetY: Int,
    ) {
        _visibleMapChunk.update {
            it.copy(
                mapOffsetX = it.mapOffsetX + offsetX,
                mapOffsetY = it.mapOffsetY + offsetY,
            ).update(mapController.map.value)
        }
    }

}
