package ru.MeatGames.roguelike.tomb.util

import ru.MeatGames.roguelike.tomb.Assets
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.model.Item
import ru.MeatGames.roguelike.tomb.model.MapClass

object MapHelper {

    var mMapWidth: Int = 0
    var mMapHeight: Int = 0

    @JvmStatic
    fun init(mapWidth: Int, mapHeight: Int) {
        mMapWidth = mapWidth
        mMapHeight = mapHeight
    }

    @JvmStatic
    fun left(coordX: Int) = coordX > -1

    @JvmStatic
    fun right(coordX: Int) = coordX < mMapWidth

    @JvmStatic
    fun top(coordY: Int) = coordY > -1

    @JvmStatic
    fun bottom(coordY: Int) = coordY < mMapHeight

    @JvmStatic
    fun horizontal(coordX: Int) = coordX > -1 && coordX < mMapWidth

    @JvmStatic
    fun vertical(coordY: Int) = coordY > -1 && coordY < mMapHeight

    @JvmStatic
    fun getMapTile(coordX: Int, coordY: Int): MapClass? {
        if (horizontal(coordX) && vertical(coordY)) {
            return GameController.getMap()[coordX][coordY]
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
        GameController.getMap()[mapX][mapY].mFloorID = floorId
        changeObject(mapX, mapY, objectId)
    }

    @JvmStatic
    fun changeObject(mapX: Int, mapY: Int, objectId: Int) {
        val map = GameController.getMap()

        map[mapX][mapY].mObjectID = objectId

        map[mapX][mapY].mIsPassable = Assets.objects[objectId].mIsPassable
        map[mapX][mapY].mIsTransparent = Assets.objects[objectId].mIsTransparent
        map[mapX][mapY].mIsUsable = Assets.objects[objectId].mIsUsable
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
    fun getFloorId(mapX: Int, mapY: Int) = GameController.getMap()[mapX][mapY].mFloorID

    @JvmStatic
    fun getObjectId(mapX: Int, mapY: Int) = GameController.getMap()[mapX][mapY].mObjectID

    @JvmStatic
    fun getItem(mapX: Int, mapY: Int): Item? {
        if (GameController.getMap()[mapX][mapY].mItems.size != 0) {
            return GameController.getMap()[mapX][mapY].mItems[0]
        } else {
            return null
        }
    }

    @JvmStatic
    fun isWall(mapX: Int, mapY: Int) = (horizontal(mapX) && vertical(mapY) && GameController.getMap()[mapX][mapY].isWall)

}