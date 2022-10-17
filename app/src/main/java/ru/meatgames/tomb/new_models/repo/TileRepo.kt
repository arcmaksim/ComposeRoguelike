package ru.meatgames.tomb.new_models.repo

import android.graphics.Rect
import ru.meatgames.tomb.new_models.tile.Tile

@Deprecated("To be deleted")
class TileRepo {

    val tiles: List<Tile>
        get() = throw UnsupportedOperationException()

    companion object {
        @JvmStatic
        val voidTile = Tile(
            name = "void",
            isPassable = true,
            isTransparent = true,
            isUsable = false,
            imageRect = Rect(0, 0, 24, 24),
        )
        @JvmStatic
        val emptyTile = Tile(
            name = "nothing",
            isPassable = true,
            isTransparent = true,
            isUsable = false,
            imageRect = Rect(0, 0, 24, 24),
        )
    }

}
