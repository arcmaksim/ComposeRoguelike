package ru.meatgames.tomb

import ru.meatgames.tomb.db.RoomDBClass
import ru.meatgames.tomb.model.MapClass
import ru.meatgames.tomb.model.RoomClass
import ru.meatgames.tomb.util.MapHelper
import java.util.*

class MapGenerator {

    private var roomPrototypes: Array<RoomClass?> = arrayOfNulls(16)
    private var placedRoomsData: Array<RoomDBClass?>

    var currentRoom: Array<IntArray>? = null
    val rnd: Random = Random()
    var m: Int = 0
    var n: Int = 0

    var possibleNextRoomX = 0
    var possibleNextRoomY = 0
    private val mMaxRooms = 70
    var xl: Int = 0
    var xr: Int = 0
    var yl: Int = 0
    var yr: Int = 0

    init {
        placedRoomsData = arrayOfNulls(mMaxRooms)
        loadingRooms()
    }

    fun loadingRooms() {
        roomPrototypes[0] = RoomClass(arrayOf(
                intArrayOf(4001, 4000, 4012),
                intArrayOf(4012, 4000, 4009),
                intArrayOf(4009, 4000, 4012),
                intArrayOf(4012, 4000, 4001)))
        roomPrototypes[1] = RoomClass(arrayOf(
                intArrayOf(4001, 10007, 11007, 10007, 4001),
                intArrayOf(10007, 11000, 10000, 11000, 10007),
                intArrayOf(11007, 10000, 11000, 10000, 11007),
                intArrayOf(10007, 11000, 10000, 11000, 10007),
                intArrayOf(4001, 10007, 11007, 10007, 4001)))
        roomPrototypes[2] = RoomClass(arrayOf(
                intArrayOf(4001, 4001, 4000, 4000, 4012, 4001),
                intArrayOf(4000, 4000, 4000, 4012, 4009, 4012),
                intArrayOf(4000, 4012, 4000, 4000, 4012, 4000),
                intArrayOf(4012, 4009, 4012, 4000, 4000, 4000),
                intArrayOf(4012, 4009, 4012, 4000, 4000, 4000),
                intArrayOf(4000, 4012, 4000, 4000, 4012, 4000),
                intArrayOf(4000, 4000, 4000, 4012, 4009, 4012),
                intArrayOf(4001, 4001, 4000, 4000, 4012, 4001)))
        roomPrototypes[3] = RoomClass(arrayOf(
                intArrayOf(4001, 4001, 4004, 4001, 4001),
                intArrayOf(4004, 4000, 4000, 4000, 4004),
                intArrayOf(4001, 4000, 4013, 4000, 4001),
                intArrayOf(4004, 4000, 4000, 4000, 4004),
                intArrayOf(4001, 4001, 4004, 4001, 4001)))
        roomPrototypes[4] = RoomClass(arrayOf(
                intArrayOf(4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 5000, 5004, 5000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000)))
        roomPrototypes[5] = RoomClass(arrayOf(
                intArrayOf(5007, 5000, 5012, 5001),
                intArrayOf(5007, 5000, 5009, 5012),
                intArrayOf(5007, 5000, 5000, 5000),
                intArrayOf(5001, 5007, 5007, 5007)))
        roomPrototypes[6] = RoomClass(arrayOf(
                intArrayOf(10001, 11007, 10007, 11007, 10007, 11007, 10007, 11007, 10001),
                intArrayOf(11007, 10000, 11000, 10000, 11000, 10000, 11000, 10000, 11007),
                intArrayOf(10007, 11000, 10007, 11007, 10000, 11007, 10007, 11000, 10007),
                intArrayOf(11007, 10000, 11007, 10000, 11000, 10000, 11007, 10000, 11007),
                intArrayOf(10007, 11000, 10000, 11000, 10007, 11000, 10000, 11000, 10007),
                intArrayOf(11007, 10000, 11007, 10000, 11000, 10000, 11007, 10000, 11007),
                intArrayOf(10007, 11000, 10007, 11007, 10000, 11007, 10007, 11000, 10007),
                intArrayOf(11007, 10000, 11000, 10000, 11000, 10000, 11000, 10000, 11007),
                intArrayOf(10001, 11007, 10007, 11007, 10007, 11007, 10007, 11007, 10001)))
        roomPrototypes[7] = RoomClass(arrayOf(
                intArrayOf(4001, 4000, 4000, 4000, 4001),
                intArrayOf(4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4001, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000),
                intArrayOf(4001, 4000, 4000, 4000, 4001),
                intArrayOf(4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4001, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000),
                intArrayOf(4001, 4000, 4000, 4000, 4001)))
        roomPrototypes[8] = RoomClass(arrayOf(
                intArrayOf(4001, 4000, 4000, 4000, 4000, 4000, 4001),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4001, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4001, 4000, 4000, 4000, 4000, 4000, 4001)))
        roomPrototypes[9] = RoomClass(arrayOf(
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000)))
        roomPrototypes[10] = RoomClass(arrayOf(
                intArrayOf(4001, 4000, 5000, 4000, 4001),
                intArrayOf(4000, 4000, 5000, 4000, 4000),
                intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(4000, 4000, 5000, 4000, 4000),
                intArrayOf(4001, 4000, 5000, 4000, 4001)))
        roomPrototypes[11] = RoomClass(arrayOf(
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4001, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000)))
        roomPrototypes[12] = RoomClass(arrayOf(
                intArrayOf(4001, 4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001, 4001),
                intArrayOf(4001, 4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001, 4001),
                intArrayOf(4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001),
                intArrayOf(4000, 4000, 4000, 4001, 4000, 4000, 4000, 4000, 4001, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000),
                intArrayOf(4000, 4000, 4000, 4001, 4000, 4000, 4000, 4000, 4001, 4000, 4000, 4000),
                intArrayOf(4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001),
                intArrayOf(4001, 4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001, 4001),
                intArrayOf(4001, 4001, 4001, 4000, 4000, 4000, 4000, 4000, 4000, 4001, 4001, 4001)))
        roomPrototypes[13] = RoomClass(arrayOf(
                intArrayOf(10000, 11000, 10000, 11000, 10000),
                intArrayOf(11000, 10009, 11000, 10007, 11000),
                intArrayOf(10000, 11000, 10000, 11007, 10000),
                intArrayOf(11000, 10007, 11000, 10007, 11000),
                intArrayOf(10000, 11007, 10000, 11000, 10000),
                intArrayOf(11000, 10007, 11000, 10009, 11000),
                intArrayOf(10000, 11000, 10000, 11000, 10000)))
        roomPrototypes[14] = RoomClass(arrayOf(
                intArrayOf(4001, 11007, 10007, 11007),
                intArrayOf(11009, 10000, 11000, 10000),
                intArrayOf(10016, 11000, 10000, 11000),
                intArrayOf(11009, 10000, 11000, 10000),
                intArrayOf(4001, 11007, 10007, 11007)))
        roomPrototypes[15] = RoomClass(arrayOf(
                intArrayOf(8000, 9000, 8000, 9000, 8000, 9000, 8000),
                intArrayOf(9000, 8000, 9000, 8000, 9000, 8000, 9000),
                intArrayOf(8000, 9000, 8000, 9000, 8000, 9000, 8000),
                intArrayOf(9000, 8000, 9000, 8000, 9000, 8000, 9000),
                intArrayOf(8000, 9000, 8000, 9000, 8000, 9000, 8000),
                intArrayOf(9000, 8000, 9000, 8000, 9000, 8000, 9000),
                intArrayOf(8000, 9000, 8000, 9000, 8000, 9000, 8000)))
    }

    fun findCell(): Boolean {
        for (z2 in 0..19) {
            possibleNextRoomX = rnd.nextInt(xr - xl + 1) + xl
            possibleNextRoomY = rnd.nextInt(yr - yl + 1) + yl
            if (correctPlace(possibleNextRoomX, possibleNextRoomY)) return true
        }
        return false
    }

    fun correctPlace(x: Int, y: Int): Boolean {
        return MapHelper.isWall(x, y)
                &&!MapHelper.isWall(x, y - 1) xor !MapHelper.isWall(x, y + 1) xor (!MapHelper.isWall(x - 1, y) xor !MapHelper.isWall(x + 1, y))
    }

    fun checkZone(n: Int, m: Int, ln: Int, lm: Int): Boolean {
        if (n + ln > MapHelper.mMapWidth - 3 || m + lm > MapHelper.mMapHeight - 3 || n < 2 || m < 2) return false
        for (n1 in n until n + ln + 1) {
            for (m1 in m until m + lm + 1) {
                if (!MapHelper.isWall(n1, m1)) return false
            }
        }
        return true
    }

    fun deleteObjects(startMapX: Int, startMapY: Int, width: Int, height: Int) {
        for (x1 in 0 until width) {
            for (y1 in 0 until height) {
                if (!MapHelper.isWall(startMapX + x1, startMapY + y1)) {
                    MapHelper.changeObject(startMapX + x1, startMapY + y1, 0)
                }
            }
        }
    }

    fun horizontalMirror(width: Int, height: Int) {
        var temp: Int
        for (y in 0 until height / 2) {
            for (x in 0 until width) {
                temp = currentRoom!![x][y]
                currentRoom!![x][y] = currentRoom!![x][height - 1 - y]
                currentRoom!![x][height - 1 - y] = temp
            }
        }
    }

    fun verticalMirror(width: Int, height: Int) {
        var temp: Int
        for (x in 0 until width / 2) {
            for (y in 0 until height) {
                temp = currentRoom!![x][y]
                currentRoom!![x][y] = currentRoom!![width - 1 - x][y]
                currentRoom!![width - 1 - x][y] = temp
            }
        }
    }

    fun newZone(width: Int, height: Int, roomIndex: Int) {
        currentRoom = Array(width) { IntArray(height) }
        currentRoom = roomPrototypes[roomIndex]!!.map.clone()
    }

    fun newRotateZone(width: Int, height: Int, roomIndex: Int) {
        currentRoom = Array(height) { IntArray(width) }
        val temp: Array<IntArray> = roomPrototypes[roomIndex]!!.map.clone()
        for (x in 0 until width) {
            for (y in 0 until height) {
                currentRoom!![y][x] = temp[x][y]
            }
        }
    }

    fun getRoom(mapX: Int, mapY: Int): Int {
        var xx: Int = 0
        while (xx < placedRoomsData.size) {
            if (placedRoomsData[xx] != null
                    && mapX >= placedRoomsData[xx]!!.x
                    && mapY >= placedRoomsData[xx]!!.y
                    && mapX <= placedRoomsData[xx]!!.x + placedRoomsData[xx]!!.lx - 1
                    && mapY <= placedRoomsData[xx]!!.y + placedRoomsData[xx]!!.ly - 1)
                return xx
            xx++
        }
        return -1
    }

    fun generateMap() {
        var roomCount = 0

        MapHelper.fillArea(0, 0, MapHelper.mMapWidth, MapHelper.mMapHeight, 4001)

        for (i in 0 until roomCount) {
            placedRoomsData[i] = null
        }

        var mapTile: MapClass
        for (x in 0 until MapHelper.mMapWidth) {
            for (y in 0 until MapHelper.mMapHeight) {
                mapTile = MapHelper.getMapTile(x, y) as MapClass
                mapTile.deleteItems()
                mapTile.mIsDiscovered = false
                mapTile.mCurrentlyVisible = false
            }
        }

        /*while (Assets.game.firstMob != null) {
            Assets.game.firstMob.map.deleteMob()
            Assets.game.firstMob.mob = null
            Assets.game.firstMob = Assets.game.firstMob.next
        }*/

        var x2 = rnd.nextInt(MapHelper.mMapWidth / 2) + 16
        var y2 = rnd.nextInt(MapHelper.mMapHeight / 2) + 16

        var up: Boolean
        var down: Boolean
        var left: Boolean
        var right: Boolean

        GameController.initNewGame(x2, y2)

        MapHelper.fillArea(x2, y2, 5, 5, 4000)
        MapHelper.fillArea(x2 + 2, y2 + 2, 1, 1, 4010)
        placedRoomsData[roomCount] = RoomDBClass(x2, y2, 5, 5)

        xl = x2 - 1
        xr = x2 + 5
        yl = y2 - 1
        yr = y2 + 5

        var lx: Int
        var ly: Int

        while (roomCount < mMaxRooms - 1) {
            if (findCell()) {
                right = MapHelper.getMapTile(possibleNextRoomX - 1, possibleNextRoomY)!!.mIsPassable
                left = MapHelper.getMapTile(possibleNextRoomX + 1, possibleNextRoomY)!!.mIsPassable
                down = MapHelper.getMapTile(possibleNextRoomX, possibleNextRoomY - 1)!!.mIsPassable
                up = MapHelper.getMapTile(possibleNextRoomX, possibleNextRoomY + 1)!!.mIsPassable

                if (right xor left xor (down xor up)) {
                    val (n, tempLx, tempLy) = when (rnd.nextInt(100)) {
                        in 0..6 -> Triple(0, 4, 3)
                        in 7..11 -> Triple(1, 5, 5)
                        in 12..15 -> Triple(2, 8, 6)
                        in 16..18 -> Triple(3, 5, 5)
                        in 19..21 -> Triple(4, 5, 5)
                        in 22..25 -> Triple(5, 4, 4)
                        in 26..29 -> Triple(6, 9, 9)
                        in 30..34 -> Triple(7, 9, 5)
                        in 35..40 -> Triple(7, 5, 5)
                        in 41..46 -> Triple(8, 4, 4)
                        in 47..51 -> Triple(8, 7, 7)
                        in 52..60 -> Triple(9, 6, 9)
                        in 61..69 -> Triple(10, 5, 5)
                        in 70..77 -> Triple(11, 12, 16)
                        in 78..85 -> Triple(12, 12, 12)
                        in 86..91 -> Triple(13, 7, 5)
                        in 92..95 -> Triple(14, 5, 4)
                        in 96..99 -> Triple(100, rnd.nextInt(8) + 3, rnd.nextInt(8) + 3)
                        else -> Triple(0, 0, 0)
                    }

                    lx = tempLx
                    ly = tempLy

                    if (n != 100) {
                        val tmp: Int
                        when (rnd.nextInt(13)) {
                            0 -> {
                                newZone(lx, ly, n)
                                horizontalMirror(lx, ly)
                            }
                            2 -> {
                                newZone(lx, ly, n)
                                verticalMirror(lx, ly)
                            }
                            4 -> {
                                newZone(lx, ly, n)
                                verticalMirror(lx, ly)
                                horizontalMirror(lx, ly)
                            }
                            6 -> {
                                newRotateZone(lx, ly, n)
                                tmp = lx
                                lx = ly
                                ly = tmp
                            }
                            8 -> {
                                newRotateZone(lx, ly, n)
                                tmp = lx
                                lx = ly
                                ly = tmp
                                verticalMirror(lx, ly)
                            }
                            10 -> {
                                newRotateZone(lx, ly, n)
                                tmp = lx
                                lx = ly
                                ly = tmp
                                horizontalMirror(lx, ly)
                            }
                            12 -> {
                                newRotateZone(lx, ly, n)
                                tmp = lx
                                lx = ly
                                ly = tmp
                                verticalMirror(lx, ly)
                                horizontalMirror(lx, ly)
                            }
                            else -> newZone(lx, ly, n)
                        }
                    }

                    if (up) {
                        y2 = possibleNextRoomY - ly
                        if (n != 100) {
                            do {
                                x2 = possibleNextRoomX - rnd.nextInt(lx)
                            } while (currentRoom!![possibleNextRoomX - x2][ly - 1] % 1000 == 1)
                        } else {
                            x2 = possibleNextRoomX - rnd.nextInt(lx)
                        }
                    }

                    if (down) {
                        y2 = possibleNextRoomY + 1
                        if (n != 100) {
                            do {
                                x2 = possibleNextRoomX - rnd.nextInt(lx)
                            } while (currentRoom!![possibleNextRoomX - x2][0] % 1000 == 1)
                        } else {
                            x2 = possibleNextRoomX - rnd.nextInt(lx)
                        }
                    }

                    if (left) {
                        x2 = possibleNextRoomX - lx
                        if (n != 100) {
                            do {
                                y2 = possibleNextRoomY - rnd.nextInt(ly)
                            } while (currentRoom!![lx - 1][possibleNextRoomY - y2] % 1000 == 1)
                        } else {
                            y2 = possibleNextRoomY - rnd.nextInt(ly)
                        }
                    }

                    if (right) {
                        x2 = possibleNextRoomX + 1
                        if (n != 100) {
                            do {
                                y2 = possibleNextRoomY - rnd.nextInt(ly)
                            } while (currentRoom!![0][possibleNextRoomY - y2] % 1000 == 1)
                        } else {
                            y2 = possibleNextRoomY - rnd.nextInt(ly)
                        }
                    }

                    if (checkZone(x2 - 1, y2 - 1, lx + 1, ly + 1)) {
                        roomCount++
                        if (n != 100) {
                            for (x in 0 until lx) {
                                for (y in 0 until ly) {
                                    MapHelper.changeTile(x2 + x, y2 + y, currentRoom!![x][y])
                                }
                            }
                            if (up) deleteObjects(possibleNextRoomX, possibleNextRoomY - 1, 1, 1)
                            if (down) deleteObjects(possibleNextRoomX, possibleNextRoomY + 1, 1, 1)
                            if (right) deleteObjects(possibleNextRoomX + 1, possibleNextRoomY, 1, 1)
                            if (left) deleteObjects(possibleNextRoomX - 1, possibleNextRoomY, 1, 1)
                        } else {
                            MapHelper.fillArea(x2, y2, lx, ly, 4000)
                        }

                        MapHelper.fillArea(possibleNextRoomX, possibleNextRoomY, 1, 1, 4002)
                        if (x2 < xl) xl = x2 - 1
                        if (x2 + lx > xr) xr = x2 + lx + 1
                        if (xl < 2) xl = 2
                        if (xr > MapHelper.mMapWidth - 2) {
                            xr = MapHelper.mMapWidth - 2
                        }
                        if (y2 < yl) yl = y2 - 1
                        if (y2 + ly > yr) yr = y2 + ly + 1
                        if (yl < 2) yl = 2
                        if (yr > MapHelper.mMapHeight - 2) {
                            yr = MapHelper.mMapHeight - 2
                        }

                        placedRoomsData[roomCount] = RoomDBClass(x2, y2, lx, ly)
                        if (rnd.nextInt(2) == 0) {
                            if (up) {
                                val r = getRoom(possibleNextRoomX, possibleNextRoomY + 1)
                                for (x in 0 until lx)
                                    if (getRoom(x2 + x, possibleNextRoomY + 1) == r && !MapHelper.isWall(x2 + x, possibleNextRoomY + 1) && !MapHelper.isWall(x2 + x, possibleNextRoomY - 1))
                                        if (MapHelper.getFloorId(x2 + x, possibleNextRoomY + 1) == MapHelper.getFloorId(x2 + x, possibleNextRoomY - 1))
                                            MapHelper.changeObject(x2 + x, possibleNextRoomY, 0)
                            }
                            if (down) {
                                val r = getRoom(possibleNextRoomX, possibleNextRoomY - 1)
                                for (x in 0 until lx)
                                    if (getRoom(x2 + x, possibleNextRoomY - 1) == r && !MapHelper.isWall(x2 + x, possibleNextRoomY + 1) && !MapHelper.isWall(x2 + x, possibleNextRoomY - 1))
                                        if (MapHelper.getFloorId(x2 + x, possibleNextRoomY + 1) == MapHelper.getFloorId(x2 + x, possibleNextRoomY - 1))
                                            MapHelper.changeObject(x2 + x, possibleNextRoomY, 0)
                            }
                            if (right) {
                                val r = getRoom(possibleNextRoomX - 1, possibleNextRoomY)
                                for (y in 0 until ly)
                                    if (getRoom(possibleNextRoomX - 1, y2 + y) == r && !MapHelper.isWall(possibleNextRoomX + 1, y2 + y) && !MapHelper.isWall(possibleNextRoomX - 1, y2 + y))
                                        if (MapHelper.getFloorId(possibleNextRoomX + 1, y2 + y) == MapHelper.getFloorId(possibleNextRoomX - 1, y2 + y))
                                            MapHelper.changeObject(possibleNextRoomX, y2 + y, 0)
                            }
                            if (left) {
                                val r = getRoom(possibleNextRoomX + 1, possibleNextRoomY)
                                for (y in 0 until ly)
                                    if (getRoom(possibleNextRoomX + 1, y2 + y) == r && !MapHelper.isWall(possibleNextRoomX + 1, y2 + y) && !MapHelper.isWall(possibleNextRoomX - 1, y2 + y))
                                        if (MapHelper.getFloorId(possibleNextRoomX + 1, y2 + y) == MapHelper.getFloorId(possibleNextRoomX - 1, y2 + y))
                                            MapHelper.changeObject(possibleNextRoomX, y2 + y, 0)
                            }
                        }
                    }
                }
            } else
                roomCount++
            if (GameController.curLvls == GameController.maxLvl - 1 && roomCount == mMaxRooms - 2) {
                placeFinalRoom()
            }
        }
        GameController.updateLOS()
        GameController.updateZone()

        for (x in 0 until 30 + GameController.curLvls * 7) {

            do {
                x2 = rnd.nextInt(MapHelper.mMapWidth)
                y2 = rnd.nextInt(MapHelper.mMapHeight)
            } while (!MapHelper.getMapTile(x2, y2)!!.mIsPassable
                    || MapHelper.getMapTile(x2, y2)!!.mCurrentlyVisible
                    || MapHelper.getMapTile(x2, y2)!!.hasMob())

            val en = rnd.nextInt(GameController.maxMobs - GameController.curLvls - 1) + GameController.curLvls
            if (en < 3 && rnd.nextInt(3) == 0) {
                if (MapHelper.getMapTile(x2 - 1, y2)!!.mIsPassable && !MapHelper.getMapTile(x2 - 1, y2)!!.hasItem()) {
                    //GameController.createMob(x2 - 1, y2, en)
                }
                if (MapHelper.getMapTile(x2 + 1, y2)!!.mIsPassable && !MapHelper.getMapTile(x2 + 1, y2)!!.hasItem()) {
                    //GameController.createMob(x2 + 1, y2, en)
                }
            }
            //GameController.createMob(x2, y2, en)
        }

        if (GameController.curLvls < GameController.maxLvl - 1) {
            while (true) {
                x2 = rnd.nextInt(MapHelper.mMapWidth)
                y2 = rnd.nextInt(MapHelper.mMapHeight)
                if (MapHelper.getObjectId(x2, y2) == 0 && !MapHelper.getMapTile(x2, y2)!!.mCurrentlyVisible) {
                    MapHelper.changeObject(x2, y2, 11)
                    m = x2 - 2
                    n = y2 - 2
                    break
                }
            }
        }
    }

    private fun placeFinalRoom() {
        val lx = 7
        val ly = 7
        val n = 15
        var x2 = 0
        var y2 = 0
        var right: Boolean
        var left: Boolean
        var up: Boolean
        var down: Boolean
        newZone(lx, ly, n)
        while (true) {
            if (findCell()) {
                right = MapHelper.getMapTile(possibleNextRoomX - 1, possibleNextRoomY)!!.mIsPassable
                left = MapHelper.getMapTile(possibleNextRoomX + 1, possibleNextRoomY)!!.mIsPassable
                down = MapHelper.getMapTile(possibleNextRoomX, possibleNextRoomY - 1)!!.mIsPassable
                up = MapHelper.getMapTile(possibleNextRoomX, possibleNextRoomY + 1)!!.mIsPassable
                if (right xor left xor (down xor up)) {
                    if (up) {
                        y2 = possibleNextRoomY - ly
                        x2 = possibleNextRoomX - rnd.nextInt(lx)
                    }
                    if (down) {
                        y2 = possibleNextRoomY + 1
                        x2 = possibleNextRoomX - rnd.nextInt(lx)
                    }
                    if (left) {
                        x2 = possibleNextRoomX - lx
                        y2 = possibleNextRoomY - rnd.nextInt(ly)
                    }
                    if (right) {
                        x2 = possibleNextRoomX + 1
                        y2 = possibleNextRoomY - rnd.nextInt(ly)
                    }
                    if (checkZone(x2 - 1, y2 - 1, lx + 1, ly + 1)) {
                        for (x in 0 until lx) {
                            for (y in 0 until ly) {
                                MapHelper.changeTile(x2 + x, y2 + y, currentRoom!![x][y])
                            }
                        }
                        MapHelper.fillArea(possibleNextRoomX, possibleNextRoomY, 1, 1, 4002)
                        /*Assets.game.createMob(x2 + 3, y2 + 3, 5)
                        Assets.game.createMob(x2 + 4, y2 + 3, 4)
                        Assets.game.createMob(x2 + 2, y2 + 3, 4)
                        Assets.game.createMob(x2 + 3, y2 + 4, 4)
                        Assets.game.createMob(x2 + 3, y2 + 2, 4)*/
                        break
                    }
                }
            }
        }
    }

}