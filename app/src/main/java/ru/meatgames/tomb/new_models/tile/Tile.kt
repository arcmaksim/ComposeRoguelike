package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect
import androidx.annotation.DrawableRes

class Tile(
    val name: String,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
    val imageRect: Rect
) {

    constructor(
        tileData: TilePropertyJsonModel,
        image: Rect
    ) : this(
        tileData.name,
        tileData.passable,
        tileData.transparent,
        tileData.usable,
        image
    )

}