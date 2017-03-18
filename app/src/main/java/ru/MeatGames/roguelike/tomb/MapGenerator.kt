package ru.MeatGames.roguelike.tomb

import ru.MeatGames.roguelike.tomb.db.RoomDBClass
import ru.MeatGames.roguelike.tomb.model.RoomClass
import java.util.*

class MapGenerator {

    var room: Array<RoomClass?> = arrayOfNulls<RoomClass>(16)
    var room1: Array<RoomDBClass?>
    var zone: Array<IntArray>? = null
    var rnd: Random
    var m: Int = 0
    var n: Int = 0
    var z = 0
    var z1 = 0
    var rc: Int = 0
    var mr = 70
    var xl: Int = 0
    var xr: Int = 0
    var yl: Int = 0
    var yr: Int = 0

    init {
        rnd = Random()
        room1 = arrayOfNulls<RoomDBClass>(mr)
        loadingRooms()
    }

    fun loadingRooms() {
        room[0] = RoomClass(arrayOf(intArrayOf(5030, 5000, 5041),
                intArrayOf(5041, 5000, 5038),
                intArrayOf(5038, 5000, 5041),
                intArrayOf(5041, 5000, 5030)))
        room[1] = RoomClass(arrayOf(intArrayOf(5030, 11036, 12036, 11036, 5030),
                intArrayOf(11036, 12000, 11000, 12000, 11036),
                intArrayOf(12036, 11000, 12000, 11000, 12036),
                intArrayOf(11036, 12000, 11000, 12000, 11036),
                intArrayOf(5030, 11036, 12036, 11036, 5030)))
        room[2] = RoomClass(arrayOf(intArrayOf(5030, 5030, 5000, 5000, 5041, 5030),
                intArrayOf(5000, 5000, 5000, 5041, 5038, 5041),
                intArrayOf(5000, 5041, 5000, 5000, 5041, 5000),
                intArrayOf(5041, 5038, 5041, 5000, 5000, 5000),
                intArrayOf(5041, 5038, 5041, 5000, 5000, 5000),
                intArrayOf(5000, 5041, 5000, 5000, 5041, 5000),
                intArrayOf(5000, 5000, 5000, 5041, 5038, 5041),
                intArrayOf(5030, 5030, 5000, 5000, 5041, 5030)))
        room[3] = RoomClass(arrayOf(intArrayOf(5030, 5030, 5033, 5030, 5030),
                intArrayOf(5033, 5000, 5000, 5000, 5033),
                intArrayOf(5030, 5000, 5042, 5000, 5030),
                intArrayOf(5033, 5000, 5000, 5000, 5033),
                intArrayOf(5030, 5030, 5033, 5030, 5030)))
        room[4] = RoomClass(arrayOf(intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 6000, 6033, 6000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000)))
        room[5] = RoomClass(arrayOf(intArrayOf(6036, 6000, 6041, 6030),
                intArrayOf(6036, 6000, 6038, 6041),
                intArrayOf(6036, 6000, 6000, 6000),
                intArrayOf(6030, 6036, 6036, 6036)))
        room[6] = RoomClass(arrayOf(intArrayOf(11030, 12036, 11036, 12036, 11036, 12036, 11036, 12036, 11030),
                intArrayOf(12036, 11000, 12000, 11000, 12000, 11000, 12000, 11000, 12036),
                intArrayOf(11036, 12000, 11036, 12036, 11000, 12036, 11036, 12000, 11036),
                intArrayOf(12036, 11000, 12036, 11000, 12000, 11000, 12036, 11000, 12036),
                intArrayOf(11036, 12000, 11000, 12000, 11036, 12000, 11000, 12000, 11036),
                intArrayOf(12036, 11000, 12036, 11000, 12000, 11000, 12036, 11000, 12036),
                intArrayOf(11036, 12000, 11036, 12036, 11000, 12036, 11036, 12000, 11036),
                intArrayOf(12036, 11000, 12000, 11000, 12000, 11000, 12000, 11000, 12036),
                intArrayOf(11030, 12036, 11036, 12036, 11036, 12036, 11036, 12036, 11030)))
        room[7] = RoomClass(arrayOf(intArrayOf(5030, 5000, 5000, 5000, 5030),
                intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5030, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(5030, 5000, 5000, 5000, 5030),
                intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5030, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000),
                intArrayOf(5030, 5000, 5000, 5000, 5030)))
        room[8] = RoomClass(arrayOf(intArrayOf(5030, 5000, 5000, 5000, 5000, 5000, 5030),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5030, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5030, 5000, 5000, 5000, 5000, 5000, 5030)))
        room[9] = RoomClass(arrayOf(intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 6000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 6000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 6000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 6000, 6000, 6000, 6000, 6000, 6000, 6000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000)))
        room[10] = RoomClass(arrayOf(intArrayOf(5030, 5000, 6000, 5000, 5030),
                intArrayOf(5000, 5000, 6000, 5000, 5000),
                intArrayOf(6000, 6000, 6000, 6000, 6000),
                intArrayOf(5000, 5000, 6000, 5000, 5000),
                intArrayOf(5030, 5000, 6000, 5000, 5030)))
        room[11] = RoomClass(arrayOf(intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5030, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000)))
        room[12] = RoomClass(arrayOf(intArrayOf(5030, 5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030, 5030),
                intArrayOf(5030, 5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030, 5030),
                intArrayOf(5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030),
                intArrayOf(5000, 5000, 5000, 5030, 5000, 5000, 5000, 5000, 5030, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000),
                intArrayOf(5000, 5000, 5000, 5030, 5000, 5000, 5000, 5000, 5030, 5000, 5000, 5000),
                intArrayOf(5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030),
                intArrayOf(5030, 5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030, 5030),
                intArrayOf(5030, 5030, 5030, 5000, 5000, 5000, 5000, 5000, 5000, 5030, 5030, 5030)))
        room[13] = RoomClass(arrayOf(intArrayOf(11000, 12000, 11000, 12000, 11000),
                intArrayOf(12000, 11038, 12000, 11036, 12000),
                intArrayOf(11000, 12000, 11000, 12036, 11000),
                intArrayOf(12000, 11036, 12000, 11036, 12000),
                intArrayOf(11000, 12036, 11000, 12000, 11000),
                intArrayOf(12000, 11036, 12000, 11038, 12000),
                intArrayOf(11000, 12000, 11000, 12000, 11000)))
        room[14] = RoomClass(arrayOf(intArrayOf(5030, 12036, 11036, 12036),
                intArrayOf(12038, 11000, 12000, 11000),
                intArrayOf(11045, 12000, 11000, 12000),
                intArrayOf(12038, 11000, 12000, 11000),
                intArrayOf(5030, 12036, 11036, 12036)))
        room[15] = RoomClass(arrayOf(intArrayOf(9000, 10000, 9000, 10000, 9000, 10000, 9000),
                intArrayOf(10000, 9000, 10000, 9000, 10000, 9000, 10000),
                intArrayOf(9000, 10000, 9000, 10000, 9000, 10000, 9000),
                intArrayOf(10000, 9000, 10000, 9000, 10000, 9000, 10000),
                intArrayOf(9000, 10000, 9000, 10000, 9000, 10000, 9000),
                intArrayOf(10000, 9000, 10000, 9000, 10000, 9000, 10000),
                intArrayOf(9000, 10000, 9000, 10000, 9000, 10000, 9000)))
    }

    fun correctPlace(x: Int, y: Int): Boolean {
        return Global.map!![x][y].isWall && !Global.map!![x][y - 1].isWall xor !Global.map!![x][y + 1].isWall xor (!Global.map!![x - 1][y].isWall xor !Global.map!![x + 1][y].isWall)
    }

    fun findCell(): Boolean {
        for (z2 in 0..19) {
            z = rnd.nextInt(xr - xl + 1) + xl
            z1 = rnd.nextInt(yr - yl - 1) + yl
            if (correctPlace(z, z1))
                return true
        }
        return false
    }

    fun checkZone(n: Int, m: Int, ln: Int, lm: Int): Boolean {
        if (n + ln > Global.game.mapWidth - 3 || m + lm > Global.game.mapHeight - 3 || n < 2 || m < 2)
            return false
        for (n1 in n..n + ln + 1 - 1)
            for (m1 in m..m + lm + 1 - 1)
                if (!Global.map!![n1][m1].isWall)
                    return false
        return true
    }

    fun fillArea(sx: Int, sy: Int, lx1: Int, ly1: Int, f: Int) {
        for (y in sy..sy + ly1 - 1)
            for (x in sx..sx + lx1 - 1) {
                Global.map!![x][y].mFloorID = f / 1000
                Global.map!![x][y].mObjectID = f % 1000
                modifyTile(x, y, f / 1000, f % 1000)
            }
    }

    fun modifyTile(px: Int, py: Int, f: Int, o: Int) {
        Global.map!![px][py].mIsPassable = Global.tiles[o].mIsPassable
        Global.map!![px][py].mIsTransparent = Global.tiles[o].mIsTransparent
        Global.map!![px][py].mIsUsable = Global.tiles[o].mIsUsable
    }

    fun deleteObjects(x: Int, y: Int, lx: Int, ly: Int) {
        for (x1 in 0..lx - 1)
            for (y1 in 0..ly - 1)
                if (!Global.map!![x + x1][y + y1].isWall) {
                    Global.map!![x + x1][y + y1].mObjectID = 0
                    modifyTile(x + x1, y + y1, Global.map!![x + x1][y + y1].mFloorID, 0)
                }
    }

    fun horizontalMirror(lx: Int, ly: Int) {
        var temp: Int
        for (y in 0..ly / 2 - 1)
            for (x in 0..lx - 1) {
                temp = zone!![x][y]
                zone!![x][y] = zone!![x][ly - 1 - y]
                zone!![x][ly - 1 - y] = temp
            }
    }

    fun verticalMirror(lx: Int, ly: Int) {
        var temp: Int
        for (x in 0..lx / 2 - 1)
            for (y in 0..ly - 1) {
                temp = zone!![x][y]
                zone!![x][y] = zone!![lx - 1 - x][y]
                zone!![lx - 1 - x][y] = temp
            }
    }

    fun newZone(lx: Int, ly: Int, n: Int) {
        zone = Array(lx) { IntArray(ly) }
        zone = room[n]!!.map.clone()
    }

    fun newRotateZone(lx: Int, ly: Int, n: Int) {
        zone = Array(ly) { IntArray(lx) }
        val temp: Array<IntArray> = room[n]!!.map.clone()
        for (x in 0..lx - 1)
            for (y in 0..ly - 1)
                zone!![y][x] = temp[x][y]
    }

    fun getRoom(x: Int, y: Int): Int {
        var xx: Int = 0
        while (xx < room1.size) {
            if (room1[xx] != null && x >= room1[xx]!!.x && y >= room1[xx]!!.y && x <= room1[xx]!!.x + room1[xx]!!.lx - 1 && y <= room1[xx]!!.y + room1[xx]!!.ly - 1)
                return xx
            xx++
        }
        return -1
    }

    fun generateMap() {
        rc = 0
        var lx: Int
        var ly: Int
        fillArea(0, 0, Global.game.mapWidth, Global.game.mapHeight, 5030)
        for (i in 0..rc - 1)
            room1[i] = null
        for (x in 0..Global.game.mapWidth - 1)
            for (y in 0..Global.game.mapHeight - 1) {
                Global.map!![x][y].deleteItems()
                Global.map!![x][y].mIsDiscovered = false
                Global.map!![x][y].mCurrentlyVisible = false
            }
        while (Global.game.firstMob != null) {
            Global.game.firstMob.map.deleteMob()
            Global.game.firstMob.mob = null
            Global.game.firstMob = Global.game.firstMob.next
        }
        var x2 = 0
        var y2 = 0
        var up: Boolean
        var down: Boolean
        var left: Boolean
        var right: Boolean
        lx = rnd.nextInt(Global.game.mapWidth / 2) + 16
        ly = rnd.nextInt(Global.game.mapHeight / 2) + 16
        Global.mapview.camx = lx - 2
        Global.mapview.camy = ly - 2
        Global.hero!!.mx = lx + 2
        Global.hero!!.my = ly + 2
        fillArea(lx, ly, 5, 5, 5000)
        fillArea(lx + 2, ly + 2, 1, 1, 5039)
        room1[rc] = RoomDBClass(x2, y2, lx, ly)
        xl = lx - 1
        xr = lx + 5
        yl = ly - 1
        yr = ly + 5
        while (rc < mr - 1) {
            if (findCell()) {
                right = Global.map!![z - 1][z1].mIsPassable
                left = Global.map!![z + 1][z1].mIsPassable
                down = Global.map!![z][z1 - 1].mIsPassable
                up = Global.map!![z][z1 + 1].mIsPassable
                if (right xor left xor (down xor up)) {
                    var n = 0
                    when(rnd.nextInt(100)) {
                        in 0..6 -> {
                            lx = 4
                            ly = 3
                            n = 0
                        }
                        in 7..11 -> {
                            ly = 5
                            lx = ly
                            n = 1
                        }
                        in 12..15 -> {
                            lx = 8
                            ly = 6
                            n = 2
                        }
                        in 16..18 -> {
                            ly = 5
                            lx = ly
                            n = 3
                        }
                        in 19..21 -> {
                            ly = 5
                            lx = ly
                            n = 4
                        }
                        in 22..25 -> {
                            ly = 4
                            lx = ly
                            n = 5
                        }
                        in 26..29 -> {
                            ly = 9
                            lx = ly
                            n = 6
                        }
                        in 30..34 -> {
                            lx = 9
                            ly = 5
                            n = 7
                        }
                        in 35..40 -> {
                            ly = 5
                            lx = ly
                            n = 7
                        }
                        in 41..46 -> {
                            ly = 4
                            lx = ly
                            n = 8
                        }
                        in 47..51 -> {
                            ly = 7
                            lx = ly
                            n = 8
                        }
                        in 52..60 -> {
                            lx = 6
                            ly = 9
                            n = 9
                        }
                        in 61..69 -> {
                            ly = 5
                            lx = ly
                            n = 10
                        }
                        in 70..77 -> {
                            lx = 12
                            ly = 16
                            n = 11
                        }
                        in 78..85 -> {
                            n = 12
                            ly = n
                            lx = ly
                        }
                        in 86..91 -> {
                            lx = 7
                            ly = 5
                            n = 13
                        }
                        in 92..95 -> {
                            lx = 5
                            ly = 4
                            n = 14
                        }
                        in 96..99 -> {
                            lx = rnd.nextInt(8) + 3
                            ly = rnd.nextInt(8) + 3
                            n = 100
                        }
                    }
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
                        y2 = z1 - ly
                        if (n != 100) {
                            do {
                                x2 = z - rnd.nextInt(lx)
                            } while (zone!![z - x2][ly - 1] % 1000 == 30)
                        } else {
                            x2 = z - rnd.nextInt(lx)
                        }
                    }
                    if (down) {
                        y2 = z1 + 1
                        if (n != 100) {
                            do {
                                x2 = z - rnd.nextInt(lx)
                            } while (zone!![z - x2][0] % 1000 == 30)
                        } else {
                            x2 = z - rnd.nextInt(lx)
                        }
                    }
                    if (left) {
                        x2 = z - lx
                        if (n != 100) {
                            do {
                                y2 = z1 - rnd.nextInt(ly)
                            } while (zone!![lx - 1][z1 - y2] % 1000 == 30)
                        } else {
                            y2 = z1 - rnd.nextInt(ly)
                        }
                    }
                    if (right) {
                        x2 = z + 1
                        if (n != 100) {
                            do {
                                y2 = z1 - rnd.nextInt(ly)
                            } while (zone!![0][z1 - y2] % 1000 == 30)
                        } else {
                            y2 = z1 - rnd.nextInt(ly)
                        }
                    }
                    if (checkZone(x2 - 1, y2 - 1, lx + 1, ly + 1)) {
                        rc++
                        if (n != 100) {
                            for (x in 0..lx - 1)
                                for (y in 0..ly - 1) {
                                    val v = zone!![x][y]
                                    Global.map!![x2 + x][y2 + y].mFloorID = v / 1000
                                    Global.map!![x2 + x][y2 + y].mObjectID = v % 1000
                                    modifyTile(x2 + x, y2 + y, v / 1000, v % 1000)
                                }
                            if (up) deleteObjects(z, z1 - 1, 1, 1)
                            if (down) deleteObjects(z, z1 + 1, 1, 1)
                            if (right) deleteObjects(z + 1, z1, 1, 1)
                            if (left) deleteObjects(z - 1, z1, 1, 1)
                        } else
                            fillArea(x2, y2, lx, ly, 5000)
                        fillArea(z, z1, 1, 1, 5031)
                        if (x2 < xl) xl = x2 - 1
                        if (x2 + lx > xr) xr = x2 + lx + 1
                        if (xl < 2) xl = 2
                        if (xr > Global.game.mapWidth - 2)
                            xr = Global.game.mapWidth - 2
                        if (y2 < yl) yl = y2 - 1
                        if (y2 + ly > yr) yr = y2 + ly + 1
                        if (yl < 2) yl = 2
                        if (yr > Global.game.mapHeight - 2)
                            yr = Global.game.mapHeight - 2
                        room1[rc] = RoomDBClass(x2, y2, lx, ly)
                        if (rnd.nextInt(2) == 0) {
                            if (up) {
                                val r = getRoom(z, z1 + 1)
                                for (x in 0..lx - 1)
                                    if (getRoom(x2 + x, z1 + 1) == r && !Global.map!![x2 + x][z1 + 1].isWall && !Global.map!![x2 + x][z1 - 1].isWall)
                                        if (Global.map!![x2 + x][z1 + 1].mFloorID == Global.map!![x2 + x][z1 - 1].mFloorID)
                                            Global.game.fillArea(x2 + x, z1, 1, 1, Global.map!![x2 + x][z1 + 1].mFloorID, 0)
                            }
                            if (down) {
                                val r = getRoom(z, z1 - 1)
                                for (x in 0..lx - 1)
                                    if (getRoom(x2 + x, z1 - 1) == r && !Global.map!![x2 + x][z1 + 1].isWall && !Global.map!![x2 + x][z1 - 1].isWall)
                                        if (Global.map!![x2 + x][z1 + 1].mFloorID == Global.map!![x2 + x][z1 - 1].mFloorID)
                                            Global.game.fillArea(x2 + x, z1, 1, 1, Global.map!![x2 + x][z1 + 1].mFloorID, 0)
                            }
                            if (right) {
                                val r = getRoom(z - 1, z1)
                                for (y in 0..ly - 1)
                                    if (getRoom(z - 1, y2 + y) == r && !Global.map!![z + 1][y2 + y].isWall && !Global.map!![z - 1][y2 + y].isWall)
                                        if (Global.map!![z + 1][y2 + y].mFloorID == Global.map!![z - 1][y2 + y].mFloorID)
                                            Global.game.fillArea(z, y2 + y, 1, 1, Global.map!![z + 1][y2 + y].mFloorID, 0)
                            }
                            if (left) {
                                val r = getRoom(z + 1, z1)
                                for (y in 0..ly - 1)
                                    if (getRoom(z + 1, y2 + y) == r && !Global.map!![z + 1][y2 + y].isWall && !Global.map!![z - 1][y2 + y].isWall)
                                        if (Global.map!![z + 1][y2 + y].mFloorID == Global.map!![z - 1][y2 + y].mFloorID)
                                            Global.game.fillArea(z, y2 + y, 1, 1, Global.map!![z + 1][y2 + y].mFloorID, 0)
                            }
                        }
                    }
                }
            } else
                rc++
            if (Game.curLvls == Global.game.maxLvl - 1 && rc == mr - 2)
                placeFinalRoom()
        }
        Global.mapview.calculateLineOfSight(Global.hero!!.mx, Global.hero!!.my)
        Global.game.updateZone()
        var x4: Int
        var y4: Int
        for (x in 0..30 + Game.curLvls * 7 - 1) {
            do {
                x4 = rnd.nextInt(Global.game.mapWidth)
                y4 = rnd.nextInt(Global.game.mapHeight)
            } while (!Global.map!![x4][y4].mIsPassable || Global.map!![x4][y4].mCurrentlyVisible || Global.map!![x4][y4].hasMob())
            val en = rnd.nextInt(Global.game.maxMobs - Game.curLvls - 1) + Game.curLvls
            if (en < 3 && rnd.nextInt(3) == 0) {
                if (Global.map!![x4 - 1][y4].mIsPassable && !Global.map!![x4 - 1][y4].hasItem())
                    Global.game.createMob(x4 - 1, y4, en)
                if (Global.map!![x4 + 1][y4].mIsPassable && !Global.map!![x4 + 1][y4].hasItem())
                    Global.game.createMob(x4 + 1, y4, en)
            }
            Global.game.createMob(x4, y4, en)
        }
        if (Game.curLvls < Global.game.maxLvl - 1) {
            while (true) {
                x4 = rnd.nextInt(Global.game.mapWidth)
                y4 = rnd.nextInt(Global.game.mapHeight)
                if (Global.map!![x4][y4].mObjectID == 0 && !Global.map!![x4][y4].mCurrentlyVisible) {
                    Global.map!![x4][y4].mObjectID = 40
                    m = x4 - 2
                    n = y4 - 2
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
                right = Global.map!![z - 1][z1].mIsPassable
                left = Global.map!![z + 1][z1].mIsPassable
                down = Global.map!![z][z1 - 1].mIsPassable
                up = Global.map!![z][z1 + 1].mIsPassable
                if (right xor left xor (down xor up)) {
                    if (up) {
                        y2 = z1 - ly
                        x2 = z - rnd.nextInt(lx)
                    }
                    if (down) {
                        y2 = z1 + 1
                        x2 = z - rnd.nextInt(lx)
                    }
                    if (left) {
                        x2 = z - lx
                        y2 = z1 - rnd.nextInt(ly)
                    }
                    if (right) {
                        x2 = z + 1
                        y2 = z1 - rnd.nextInt(ly)
                    }
                    if (checkZone(x2 - 1, y2 - 1, lx + 1, ly + 1)) {
                        rc++
                        for (x in 0..lx - 1)
                            for (y in 0..ly - 1) {
                                val v = zone!![x][y]
                                Global.map!![x2 + x][y2 + y].mFloorID = v / 1000
                                Global.map!![x2 + x][y2 + y].mObjectID = v % 1000
                                modifyTile(x2 + x, y2 + y, v / 1000, v % 1000)
                            }
                        fillArea(z, z1, 1, 1, 5031)
                        Global.game.createMob(x2 + 3, y2 + 3, 5)
                        Global.game.createMob(x2 + 4, y2 + 3, 4)
                        Global.game.createMob(x2 + 2, y2 + 3, 4)
                        Global.game.createMob(x2 + 3, y2 + 4, 4)
                        Global.game.createMob(x2 + 3, y2 + 2, 4)
                        break
                    }
                }
            }
        }
    }

}