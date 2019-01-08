package ru.meatgames.tomb

import android.content.Context
import ru.meatgames.tomb.map_generation.NewMapGenerator
import ru.meatgames.tomb.new_models.map.MapTile
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.util.MapHelper

class NewMapController {

    private var mMapWidth: Int = 96
    private var mMapHeight: Int = 96
    private lateinit var mMap: Array<MapTile>


    fun getMap(): Array<MapTile> = mMap

    fun getMapTile(x: Int, y: Int): MapTile? =
            if (x < 0 || y < 0 || x >= mMapWidth || y >= mMapHeight) null
            else mMap[x + y * mMapWidth]

    fun changeFloorTile(x: Int, y: Int, tile: Tile) {
        mMap[x + y * mMapWidth].floorTile = tile
    }

    fun changeObjectTile(x: Int, y: Int, tile: Tile) {
        if (tile != TileRepo.voidTile) mMap[x + y * mMapWidth].objectTile = tile
    }

    fun generateNewMap(context: Context) {
        MapHelper.init(mMapWidth, mMapHeight)
        mMap = Array(mMapWidth * mMapHeight) { MapTile() }
        val mapGenerator = NewMapGenerator(context)
        mapGenerator.generateMap()
    }

}