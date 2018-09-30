package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect

class Tile(
        private val tileData: TilePropertyJsonModel,
        val image: Rect
) {

    constructor(
            name: String,
            isPassable: Boolean,
            isTransparent: Boolean,
            isUsable: Boolean,
            image: Rect
    ) : this(
            TilePropertyJsonModel().apply {
                this.name = name
                passable = isPassable
                transparent = isTransparent
                usable = isUsable
            }, image)


    val name: String
        get() = tileData.name
    val isPassable: Boolean
        get() = tileData.passable
    val isTransparent: Boolean
        get() = tileData.transparent
    val isUsable: Boolean
        get() = tileData.usable

}