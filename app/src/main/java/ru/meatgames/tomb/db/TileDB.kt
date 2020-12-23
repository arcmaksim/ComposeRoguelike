package ru.meatgames.tomb.db

import android.graphics.Rect

class TileDB(
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
) {

    var img: Rect? = null

}