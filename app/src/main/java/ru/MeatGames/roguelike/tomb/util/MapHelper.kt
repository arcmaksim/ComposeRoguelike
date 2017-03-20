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
    fun fillArea(startX: Int, startY: Int, width: Int, height: Int, floorId: Int, objectId: Int) {
        for (y in startY..startY + height - 1) {
            for (x in startX..startX + width - 1) {
                Global.map!![x][y].mFloorID = floorId
                Global.map!![x][y].mObjectID = objectId
                modifyTile(x, y, floorId, objectId)
            }
        }
    }

    @JvmStatic
    fun fillArea(startX: Int, startY: Int, width: Int, height: Int, combinedId: Int) {
        for (y in startY..startY + height - 1) {
            for (x in startX..startX + width - 1) {
                Global.map!![x][y].mFloorID = combinedId / 1000
                Global.map!![x][y].mObjectID = combinedId % 1000
                modifyTile(x, y, combinedId / 1000, combinedId % 1000)
            }
        }
    }

    @JvmStatic
    fun modifyTile(mapX: Int, mapY: Int, floorId: Int, objectId: Int) {
        /*Global.map!![mapX][mapY].mIsPassable = Global.tiles[floorId].mIsPassable
        Global.map!![mapX][mapY].mIsTransparent = Global.tiles[floorId].mIsTransparent
        Global.map!![mapX][mapY].mIsUsable = Global.tiles[floorId].mIsUsable*/

        Global.map!![mapX][mapY].mIsPassable = Global.objects[objectId].mIsPassable
        Global.map!![mapX][mapY].mIsTransparent = Global.objects[objectId].mIsTransparent
        Global.map!![mapX][mapY].mIsUsable = Global.objects[objectId].mIsUsable
    }

}