package ru.MeatGames.roguelike.tomb.util

import ru.MeatGames.roguelike.tomb.Assets
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.model.Item
import ru.MeatGames.roguelike.tomb.model.MapClass

object MapHelper {

    var mapWidth: Int = 0
    var mapHeight: Int = 0

    @JvmStatic
    fun left(coordX: Int) = coordX > -1

    @JvmStatic
    fun right(coordX: Int) = coordX < mapWidth

    @JvmStatic
    fun top(coordY: Int) = coordY > -1

    @JvmStatic
    fun bottom(coordY: Int) = coordY < mapHeight

    @JvmStatic
    fun horizontal(coordX: Int) = coordX > -1 && coordX < mapWidth

    @JvmStatic
    fun vertical(coordY: Int) = coordY > -1 && coordY < mapHeight

    @JvmStatic
    fun getMapCell(coordX: Int, coordY: Int): MapClass? {
        if (horizontal(coordX) && vertical(coordY)) {
            return GameController.mMap[coordX][coordY]
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
        GameController.mMap[mapX][mapY].mFloorID = floorId
        changeObject(mapX, mapY, objectId)
    }

    @JvmStatic
    fun changeObject(mapX: Int, mapY: Int, objectId: Int) {
        GameController.mMap[mapX][mapY].mObjectID = objectId

        GameController.mMap[mapX][mapY].mIsPassable = Assets.objects[objectId].mIsPassable
        GameController.mMap[mapX][mapY].mIsTransparent = Assets.objects[objectId].mIsTransparent
        GameController.mMap[mapX][mapY].mIsUsable = Assets.objects[objectId].mIsUsable
    }

    @JvmStatic
    fun changeAreaObjects(startX: Int, startY: Int, width: Int, height: Int, objectId: Int) {
        for (y in startY..startY + height - 1) {
            for (x in startX..startX + width - 1) {
                changeTile(x, y, objectId)
            }
        }
    }

    @JvmStatic
    fun getFloorId(mapX: Int, mapY: Int) = GameController.mMap[mapX][mapY].mFloorID

    @JvmStatic
    fun getObjectId(mapX: Int, mapY: Int) = GameController.mMap[mapX][mapY].mObjectID

    @JvmStatic
    fun getItem(mapX: Int, mapY: Int): Item? {
        if (GameController.mMap[mapX][mapY].mItems.size != 0) {
            return GameController.mMap[mapX][mapY].mItems[0]
        } else {
            return null
        }
    }

}