package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.meatgames.tomb.*
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.tile.Tile
import kotlin.math.abs

const val viewportWidth = 11
const val viewportHeight = 11

class GameScreenViewModel : ViewModel() {

    private val mapGenerator = NewMapGenerator2(
        tileRepo = GameDataProvider.tiles,
        roomRepo = GameDataProvider.getRooms(Game.appContext),
        mapWidth = 32,
        mapHeight = 32,
    )

    private val _visibleMapChunk = MutableStateFlow(
        GameMapChunk(viewportWidth, viewportHeight, 0, 0, emptyList())
    )
    val visibleMapChunk: StateFlow<GameMapChunk> = _visibleMapChunk

    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle

    private val mapController2: NewMapController2

    init {
        val generatedMapConfig = mapGenerator.generateMap()

        _visibleMapChunk.value = GameMapChunk(
            mapOffsetX = generatedMapConfig.startingPositionX - viewportWidth / 2,
            mapOffsetY = generatedMapConfig.startingPositionY - viewportHeight / 2,
            gameMapTiles = emptyList(),
        )

        mapController2 = generatedMapConfig.mapController

        viewModelScope.launch {
            generatedMapConfig.mapController.map.collect { wrapper ->
                _visibleMapChunk.update { previousChunk ->
                    previousChunk.update(wrapper)
                }
            }
        }
    }

    private fun GameMapChunk.update(
        wrapper: MapWrapper,
    ): GameMapChunk = copy(
        gameMapTiles = (0 until viewportHeight).map { line ->
                val start = (mapOffsetY + line) * wrapper.height + mapOffsetX
                val end = start + viewportWidth
                when {
                    mapOffsetY + line !in 0 until wrapper.height -> Array(viewportWidth) { GameMapTile.voidMapTile }
                    mapOffsetX < 0 -> Array(viewportWidth) {
                        when {
                            mapOffsetX + it < 0 -> GameMapTile.voidMapTile
                            else -> wrapper.tiles[start + it]
                        }
                    }
                    mapOffsetX + viewportWidth > wrapper.width -> Array(viewportWidth) {
                        when {
                            mapOffsetX + it < wrapper.width -> wrapper.tiles[start + it]
                            else -> GameMapTile.voidMapTile
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
                tile.useObjectTile(
                    chunk.mapOffsetX + viewportWidth / 2 + offsetX,
                    chunk.mapOffsetY + viewportHeight / 2 + offsetY,
                )
            }
            chunk.gameMapTiles[index].isPassable -> {
                updateVisibleMapChunk(offsetX, offsetY)
            }
        }

        _isIdle.value = true
    }

    private fun GameMapTile.useObjectTile(
        mapX: Int,
        mapY: Int,
    ) {
        val resolvedTileReplacementOnUse = `object`?.resolveTileReplacementOnUse() ?: return

        mapController2.changeObjectTile(
            x = mapX,
            y = mapY,
            tile = resolvedTileReplacementOnUse,
        )
    }

    private fun updateVisibleMapChunk(
        offsetX: Int,
        offsetY: Int,
    ) {
        _visibleMapChunk.update {
            it.copy(
                mapOffsetX = it.mapOffsetX + offsetX,
                mapOffsetY = it.mapOffsetY + offsetY,
            ).update(mapController2.map.value)
        }
    }

    private fun Tile.resolveTileReplacementOnUse(): Tile? = when (name) {
        "door_closed" -> GameDataProvider.tiles.getTile("door_opened")
        else -> null
    }

}

data class GameMapChunk(
    val width: Int = viewportWidth,
    val height: Int = viewportHeight,
    val mapOffsetX: Int,
    val mapOffsetY: Int,
    val gameMapTiles: List<GameMapTile>,
)