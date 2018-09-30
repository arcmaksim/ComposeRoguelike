package ru.meatgames.tomb.new_models.repo

import android.content.Context
import android.graphics.Rect
import com.bluelinelabs.logansquare.LoganSquare
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.new_models.tile.TilePropertiesJsonModel
import ru.meatgames.tomb.new_models.tile.TileTextureJsonModel

class TileRepo(context: Context) {

    var tiles: List<Tile>


    init {
        val textureAtlasTileData = LoganSquare.parse(context.assets.open("images/tiles.json"), TileTextureJsonModel::class.java)
        val tileData = LoganSquare.parse(context.assets.open("data/tiles.json"), TilePropertiesJsonModel::class.java)

        tiles = List(tileData.tiles.size) { index ->
            val tile = tileData.tiles[index]
            Tile(tile, textureAtlasTileData.tiles.first { it.name == tile.name }.frame.toRect())
        }
    }


    fun getTile(tileName: String): Tile = tiles.first { it.name == tileName }


    companion object {
        @JvmStatic val voidTile = Tile(
                "void",
                true,
                true,
                false,
                Rect(0, 0, 24, 24))
        @JvmStatic val emptyTile = Tile(
                "nothing",
                true,
                true,
                false,
                Rect(0, 0, 24, 24))
    }

}