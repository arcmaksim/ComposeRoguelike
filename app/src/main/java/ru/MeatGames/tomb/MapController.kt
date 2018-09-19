package ru.meatgames.tomb

import ru.MeatGames.tomb.util.MapHelper
import ru.meatgames.tomb.model.MapClass
import ru.meatgames.tomb.util.array2d

class MapController {

    private var mMapWidth: Int = 96
    private var mMapHeight: Int = 96
    private lateinit var mMap: Array<Array<MapClass>>

    init {}

    fun getMap(): Array<Array<MapClass>> = mMap

    fun generateNewMap() {
        MapHelper.init(mMapWidth, mMapHeight)
        mMap = array2d(mMapWidth, mMapHeight) { MapClass() }
        val mapGenerator = MapGenerator()
        mapGenerator.generateMap()
    }

}