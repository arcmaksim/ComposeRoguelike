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

    @JvmStatic
    fun fillArea(startX: Int, startY: Int, width: Int, height: Int, combinedId: Int) {
        fillArea(startX, startY, width, height, combinedId / 1000, combinedId % 1000)
    }

    @JvmStatic
    fun fillArea(startX: Int, startY: Int, width: Int, height: Int, floorId: Int, objectId: Int) {
        for (y in startY..startY + height - 1) {
            for (x in startX..startX + width - 1) {
                changeTile(x, y, floorId, objectId)
            }
        }
    }

    @JvmStatic
    fun changeTile(mapX: Int, mapY: Int, combinedId: Int) {
        changeTile(mapX, mapY, combinedId / 1000, combinedId % 1000)
    }

    @JvmStatic
    fun changeTile(mapX: Int, mapY: Int, floorId: Int, objectId: Int) {
        Global.map!![mapX][mapY].mFloorID = floorId
        changeObject(mapX, mapY, objectId)
    }

    @JvmStatic
    fun changeObject(mapX: Int, mapY: Int, objectId: Int) {
        Global.map!![mapX][mapY].mObjectID = objectId

        Global.map!![mapX][mapY].mIsPassable = Global.objects[objectId].mIsPassable
        Global.map!![mapX][mapY].mIsTransparent = Global.objects[objectId].mIsTransparent
        Global.map!![mapX][mapY].mIsUsable = Global.objects[objectId].mIsUsable
    }

    @JvmStatic
    fun changeAreaObjects(startX: Int, startY: Int, width: Int, height: Int, objectId: Int) {
        for (y in startY..startY + height - 1) {
            for (x in startX..startX + width - 1) {
                changeTile(x, y, objectId)
            }
        }
    }

}