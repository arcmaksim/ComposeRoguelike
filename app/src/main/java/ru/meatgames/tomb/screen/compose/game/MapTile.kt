package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.model.tile.domain.Tile

data class MapTile(
    val floor: Tile? = null,
    val `object`: Tile? = null,
) {

    companion object {
        val voidMapTile = MapTile()
    }

    val isPassable: Boolean
        get() = floor?.isPassable ?: false && `object`?.isPassable ?: true

    val isTransparent: Boolean
        get() = floor?.isTransparent ?: true && `object`?.isTransparent ?: true

}
