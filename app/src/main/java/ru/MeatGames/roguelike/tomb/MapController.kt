package ru.MeatGames.roguelike.tomb

import ru.MeatGames.roguelike.tomb.model.MapClass
import ru.MeatGames.roguelike.tomb.util.MapHelper

class MapController {

    private var mMapWidth: Int = 96
    private var mMapHeight: Int = 96
    private lateinit var mMap: Array<Array<MapClass>>

    init {

    }

    fun getMap(): Array<Array<MapClass>> = mMap

    fun generateNewMap() {
        MapHelper.init(mMapWidth, mMapHeight)
    }

}