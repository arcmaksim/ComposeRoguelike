package ru.MeatGames.roguelike.tomb.util

import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.model.MapClass

object MapHelper {

    var mapWidth: Int = 0
    var mapHeight: Int = 0

    @JvmStatic
    fun left(coordX: Int) = coordX > -1

    @JvmStatic
    fun right(coordX: Int) = coordX < Global.mapWidth

    @JvmStatic
    fun top(coordY: Int) = coordY > -1

    @JvmStatic
    fun bottom(coordY: Int) = coordY < Global.mapHeight

    @JvmStatic
    fun horizontal(coordX: Int) = coordX > -1 && coordX < Global.mapWidth

    @JvmStatic
    fun vertical(coordY: Int) = coordY > -1 && coordY < Global.mapHeight

    @JvmStatic
    fun getMapCell(coordX: Int, coordY: Int): MapClass? {
        if (horizontal(coordX) && vertical(coordY)) {
            return Global.map!![coordX][coordY]
        } else {
            return null
        }
    }

}