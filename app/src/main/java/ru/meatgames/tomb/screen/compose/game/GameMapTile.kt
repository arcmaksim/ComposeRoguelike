package ru.meatgames.tomb.screen.compose.game

import android.graphics.Paint
import ru.meatgames.tomb.new_models.tile.Tile

data class GameMapTile(
    val floor: Tile? = null,
    val `object`: Tile? = null,
    /*var mItemId: Int = 0
    var mCreatureId: Int = 0*/
    val wallBitmapFlag: Int = -1,
    val isVisible: Boolean = false,
    val shadowPaint: Paint? = null,
    val hasItem: Boolean = false,
    val hasEnemy: Boolean = false,
) {

    companion object {
        val voidMapTile = GameMapTile()
    }

    val isPassable: Boolean
        get() = floor?.isPassable ?: false && `object`?.isPassable ?: true

}