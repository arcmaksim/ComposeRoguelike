package ru.meatgames.tomb.map_generation

import android.content.Context
import android.graphics.Rect
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.room.Room
import ru.meatgames.tomb.new_models.room.RoomRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.util.MapHelper
import java.util.*
import kotlin.collections.HashMap

private typealias Coordinate = Pair<Int, Int>

class NewMapGenerator(context: Context) {

    private val roomRepo: RoomRepo = GameDataProvider.getRooms(context)
    private var emptyMapCells: MutableList<Coordinate>
    private val candidates: HashMap<Int, ArrayList<Coordinate>> = HashMap()

    private val outerWalls: HashMap<Int, ArrayList<Coordinate>> = HashMap()
    private val outerWalls2: MutableSet<Coordinate> = mutableSetOf()
    private val mapCellCandidates = arrayListOf<Coordinate>()

    private val rnd: Random = Random()

    var mapCellSize: Int = 7
    var mapWidthInRegions: Int = 0
    var mapHeightInRegions: Int = 0
    var mapWidth: Int = 96
    var mapHeight: Int = 96

    private var mapGeneratorAnalytics: MapGeneratorAnalytics = MapGeneratorAnalytics()
    private val availableRooms: Stack<String> = Stack()
    private val availableDirections: Stack<Int> = Stack()

    /**
     * PLAN:
     * 1. Place first room
     * 2. Add 1 (or less) candidate to each candidate array list
     * 3. Pick random room and mark it as dirty for current iteration
     * 4. Pick random direction
     * 5. If picked room has outer walls associated with that direction then pick appropriate candidate list and start randomly iterate through those walls
     *      Resolve possible map cells coordinates that room could overlap
     *      If there is no records of that coordinates then place room and mark those coordinates
     * 6. If there is no more directions pick next random room and repeat from step 3
     */


    init {
        availableDirections.ensureCapacity(4)
        availableRooms.ensureCapacity(roomRepo.rooms.size)

        var counter = mapCellSize
        mapWidthInRegions = 1
        while (true) {
            counter += mapCellSize - 1
            if (counter >= MapHelper.mMapWidth) break
            mapWidthInRegions++
        }
        counter = mapCellSize
        mapHeightInRegions = 1
        while (true) {
            counter += mapCellSize - 1
            if (counter >= MapHelper.mMapHeight) break
            mapHeightInRegions++
        }

        emptyMapCells = MutableList(mapWidthInRegions * mapHeightInRegions) { i -> i % mapWidthInRegions to i / mapWidthInRegions }
        for (i in 0..3) {
            outerWalls[i] = arrayListOf()
            candidates[i] = arrayListOf()
        }
    }


    fun generateMap() {
        mapGeneratorAnalytics.clear()
        for (candidateList in candidates.values) {
            candidateList.clear()
        }

        // Clear map
        val tileRepo = GameDataProvider.tiles
        for (x in 0 until MapHelper.mMapWidth) {
            for (y in 0 until MapHelper.mMapHeight) {
                changeTile(x, y, tileRepo.getTile("floor_0"), TileRepo.voidTile)
            }
        }

        mapCellCandidates.clear()

        val getRandomMapCell = {
            mapCellCandidates[rnd.nextInt(mapCellCandidates.size)]
        }
        val removeRandomMapCell = { mapCell: Coordinate ->
            mapCellCandidates.remove(mapCell)
            emptyMapCells.remove(mapCell)
        }
        val removeRandomMapCell2 = { mapCells: List<Coordinate> ->
            for (mapCell in mapCells) {
                mapCellCandidates.remove(mapCell)
                emptyMapCells.remove(mapCell)
            }
        }
        /*val buildRoom = { room: Room, mapCell: Coordinate ->
            generateRoom(room, mapCell)
            removeRandomMapCell(mapCell)
        }*/
        val buildRoom2 = { room: Room, mapCell: Coordinate ->
            generateRoom(room, mapCell)
            removeRandomMapCell2(room.cells)
        }

        // Place initial room and initialize game
        val initialRoom = roomRepo.rooms.first()
        val initialMapCell = emptyMapCells[rnd.nextInt(emptyMapCells.size)]
        emptyMapCells.remove(initialMapCell)
        generateRoom(initialRoom, initialMapCell)
        GameController.initNewGame(
                initialMapCell.second * mapCellSize - initialMapCell.second + 1,
                initialMapCell.first * mapCellSize - initialMapCell.first + 1)

        // Generate rooms
        /*for (i in 0 until 100) {
            resetIterationData()
            var triesCount = 0
            while (true) {
                mapGeneratorAnalytics.incRoomPlacementTries()
                val mapCell = getRandomMapCell.invoke()
                val room = roomRepo.rooms.first { it.name == availableRooms.first() }
                val roomOuterWalls = arrayListOf<Coordinate>()
                for (j in 0..3) {
                    room.outerWalls[j]?.let { walls ->
                        roomOuterWalls.addAll(
                                walls.map {
                                    it.first + mapCell.first * mapCellSize - mapCell.first to it.second + mapCell.second * mapCellSize - mapCell.second
                                })
                    }
                }
                if (outerWalls2.intersect(roomOuterWalls).isNotEmpty()) {
                    buildRoom.invoke(room, mapCell)
                    break
                }

                if (triesCount++ > 9) break
            }
        }*/

        repeat(50) {
            val roomNames = roomRepo.rooms.shuffled(rnd).map { room -> room.name }
            for (roomName in roomNames) {
                resetIterationData()
                val room = roomRepo.rooms.first { it.name == roomName }
                room.getDirections(rnd).forEach { dir ->
                    val directionCells = room.connectionCells[dir]!!
                    val temp = getRandomizedCandidates(dir, rnd)
                    temp.forEach { mapCell ->
                        // check intersection
                        for (roomCell in directionCells) {
                            val normalizedMapCell = mapCell.first - roomCell.first to mapCell.second - roomCell.second
                            val isFitting = isRoomFitting(room, normalizedMapCell)
                            if (isFitting) {
                                buildRoom2.invoke(room, normalizedMapCell)
                                return@repeat
                            }
                        }
                    }
                }
            }
        }

        // Remove walls between rooms
        val temp = arrayListOf<Coordinate>()
        for (i in 0..3) {
            outerWalls[i]?.let { walls -> temp.addAll(walls) }
        }
        val filtered = temp.asSequence()
                .groupBy { it.first + it.second * mapWidth }
                .filter { it.value.size > 1 }

        for (coordinate in filtered.keys) {
            GameController.changeObjectTile(
                    coordinate % mapWidth,
                    coordinate / mapWidth,
                    TileRepo.emptyTile)
        }

        // Log statistics
        mapGeneratorAnalytics.printStatistics()
    }


    private fun generateRoom(room: Room, mapCell: Coordinate) {
        val mapArea = cellsToRect(room.cells).apply {
            top += mapCell.first
            bottom += mapCell.first
            left += mapCell.second
            right += mapCell.second
        }

        val walls = placeRoom(room, mapArea)

        for (j in 0..3) {
            walls[j]?.let { newWalls ->
                outerWalls[j]?.addAll(newWalls)
                outerWalls2.addAll(newWalls)
            }
        }

        fun convert(coordinate: Coordinate) =
                coordinate.first / mapCellSize + mapCell.first to
                        coordinate.second / mapCellSize + mapCell.second

        room.outerWalls[0]
                ?.groupBy { convert(it) }
                ?.keys
                ?.forEach { outerWall ->
                    if (outerWall.second != 0)
                        candidates[0]!!.add(outerWall.first to outerWall.second - 1)
                }
        room.outerWalls[1]
                ?.groupBy { convert(it) }
                ?.keys
                ?.forEach { outerWall ->
                    if (outerWall.first != mapWidthInRegions)
                        candidates[1]!!.add(outerWall.first + 1 to outerWall.second)
                }
        room.outerWalls[2]
                ?.groupBy { convert(it) }
                ?.keys
                ?.forEach { outerWall ->
                    if (outerWall.second != mapHeightInRegions)
                        candidates[2]!!.add(outerWall.first to outerWall.second + 1)
                }
        room.outerWalls[3]
                ?.groupBy { convert(it) }
                ?.keys
                ?.forEach { outerWall ->
                    if (outerWall.first != 0)
                        candidates[3]!!.add(outerWall.first - 1 to outerWall.second)
                }

        mapGeneratorAnalytics.incPlacedRooms()
    }

    private fun getCandidate(coordinate: Coordinate): Coordinate? =
            emptyMapCells
                    .firstOrNull { it.first == coordinate.first && it.second == coordinate.second }
                    ?.apply {
                        mapCellCandidates.add(this)
                        emptyMapCells.remove(this)
                    }

    private fun cellsToRect(mapCells: List<Coordinate>): Rect {
        return Rect(
                mapCells.minBy { it.first }!!.first,
                mapCells.minBy { it.second }!!.second,
                mapCells.maxBy { it.first }!!.first,
                mapCells.maxBy { it.second }!!.second)
    }

    private fun placeRoom(room: Room, area: Rect): HashMap<Int, ArrayList<Coordinate>> {
        val mapX = area.left * mapCellSize - area.left
        val mapY = area.top * mapCellSize - area.top

        for (h in 0 until room.height) {
            for (w in 0 until room.width) {
                val tileObject = room.objectTiles[room.objects[h][w].toString()]!!
                if (tileObject != TileRepo.voidTile) {
                    changeTile(mapX + w,
                            mapY + h,
                            room.floorTiles[room.floor[h][w].toString()]!!,
                            tileObject)
                }
            }
        }

        val outerWalls: HashMap<Int, ArrayList<Coordinate>> = hashMapOf()
        for (i in 0..3) {
            room.outerWalls[i]?.let {
                val walls = arrayListOf<Coordinate>()
                for (wall in it) {
                    walls.add(wall.first + mapX to wall.second + mapY)
                }
                outerWalls.put(i, walls)
            }
        }
        return outerWalls
    }

    private fun changeTile(mapX: Int, mapY: Int, floorTile: Tile, objectTile: Tile) {
        GameController.changeFloorTile(mapX, mapY, floorTile)
        GameController.changeObjectTile(mapX, mapY, objectTile)
    }

    private fun resetIterationData() {
        resetDirections()
        resetRooms()
    }

    private fun resetDirections() {
        availableDirections.clear()
        availableDirections.addAll(listOf(0, 1, 2, 3).shuffled(rnd))
    }

    private fun resetRooms() {
        availableRooms.clear()
        availableRooms.addAll(roomRepo.rooms.shuffled(rnd).map { it.name })
    }

    private fun getRandomizedCandidates(direction: Int, rnd: Random): Stack<Coordinate> =
            Stack<Coordinate>().apply {
                candidates[direction]?.let { addAll(it.shuffled(rnd)) }
            }

    private fun isRoomFitting(room: Room, mapCell: Coordinate): Boolean {
        var isIntersecting = false
        for (roomCell in room.cells) {
            val targetCell = mapCell.first + roomCell.first to mapCell.second + roomCell.second
            if (emptyMapCells.any { it == targetCell }) isIntersecting = true
        }
        return isIntersecting
    }

}