package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect

@Deprecated("To be deleted")
class Tile(
    val name: String,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
    val imageRect: Rect,
)
