package ru.meatgames.tomb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTile
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.screen.compose.game.GameMapTile
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile

class ThemedMapController(
    val mapWidth: Int,
    val mapHeight: Int,
) {

    private val _map: MutableStateFlow<ThemedMapWrapper> = MutableStateFlow(
        ThemedMapWrapper(
            width = mapWidth,
            height = mapHeight,
            tiles = Array(mapWidth * mapHeight) { ThemedGameMapTile() },
            updateTime = System.currentTimeMillis(),
        )
    )
    val map: StateFlow<ThemedMapWrapper> = _map

    fun getTile(
        x: Int,
        y: Int,
    ): ThemedGameMapTile? = map.value.tiles.getOrNull(x + y * mapWidth)

    fun changeFloorTile(
        x: Int,
        y: Int,
        tile: ThemedTile,
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
        tile: ThemedTile,
    ) {
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

data class ThemedMapWrapper(
    val tiles: Array<ThemedGameMapTile>,
    val width: Int,
    val height: Int,
    val updateTime: Long,
) {
    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThemedMapWrapper

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