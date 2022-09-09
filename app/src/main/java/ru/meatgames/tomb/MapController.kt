package ru.meatgames.tomb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.screen.compose.game.GameMapTile

class MapController(
    val mapWidth: Int,
    val mapHeight: Int,
) {

    private val _map: MutableStateFlow<MapWrapper> = MutableStateFlow(
        MapWrapper(
            width = mapWidth,
            height = mapHeight,
            tiles = Array(mapWidth * mapHeight) { GameMapTile() },
            updateTime = System.currentTimeMillis(),
        )
    )
    val map: StateFlow<MapWrapper> = _map

    fun getTile(
        x: Int,
        y: Int,
    ): GameMapTile? = map.value.tiles.getOrNull(x + y * mapWidth)

    fun changeFloorTile(
        x: Int,
        y: Int,
        tile: Tile,
    ) {
        val index = x + y * mapWidth
        if (index !in map.value.tiles.indices) return

        _map.update {
            it.tiles[index] = it.tiles[index].copy(
                floor = tile,
            )
            it.copy(updateTime = System.currentTimeMillis())
        }
    }

    fun changeObjectTile(
        x: Int,
        y: Int,
        tile: Tile,
    ) {
        if (tile == TileRepo.voidTile) return

        val index = x + y * mapWidth
        if (index !in map.value.tiles.indices) return

        _map.update {
            it.tiles[index] = it.tiles[index].copy(
                `object` = tile,
            )
            it.copy(updateTime = System.currentTimeMillis())
        }
    }

}

data class MapWrapper(
    val tiles: Array<GameMapTile>,
    val width: Int,
    val height: Int,
    val updateTime: Long,
) {
    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapWrapper

        if (!tiles.contentEquals(other.tiles)) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (updateTime != other.updateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tiles.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + updateTime.hashCode()
        return result
    }
}