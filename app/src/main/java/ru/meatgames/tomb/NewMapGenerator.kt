package ru.meatgames.tomb

import android.content.Context
import android.graphics.Rect
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
	private var mapAvailability: MutableList<Coordinate>

	private val outerWalls: HashMap<Int, ArrayList<Coordinate>> = HashMap()
	private val mapCellCandidates = arrayListOf<Coordinate>()

	private val rnd: Random = Random()

	var mapCellSize: Int = 7
	var mapWidth: Int = 0
	var mapHeight: Int = 0


	init {
		var counter = mapCellSize
		mapWidth = 1
		while (true) {
			counter += mapCellSize - 1
			if (counter >= MapHelper.mMapWidth) break
			mapWidth++
		}
		counter = mapCellSize
		mapHeight = 1
		while (true) {
			counter += mapCellSize - 1
			if (counter >= MapHelper.mMapHeight) break
			mapHeight++
		}

		mapAvailability = MutableList(mapWidth * mapHeight) { i -> i % mapWidth to i / mapWidth }
		for (i in 0..3) outerWalls[i] = arrayListOf()
	}


	fun generateMap() {
		// Clear map
		val tileRepo = GameDataProvider.tiles
		for (x in 0 until MapHelper.mMapWidth) {
			for (y in 0 until MapHelper.mMapHeight) {
				changeTile(x, y, tileRepo.getTile("floor_0"), TileRepo.voidTile)
			}
		}

		mapCellCandidates.clear()

		val randomMapCell = {
			val randomMapCell = mapCellCandidates[rnd.nextInt(mapCellCandidates.size)]
			mapCellCandidates.remove(randomMapCell)
			mapAvailability.remove(randomMapCell)
			randomMapCell
		}

		// Place initial room and initialize game
		val initialRoom = roomRepo.rooms.first()
		val initialMapCell = mapAvailability[rnd.nextInt(mapAvailability.size)]
		mapAvailability.remove(initialMapCell)

		// Generate rooms
		generateRoom(initialRoom, initialMapCell)
		GameController.initNewGame(initialMapCell.first * mapCellSize - initialMapCell.first + 1,
				initialMapCell.second * mapCellSize - initialMapCell.second + 1)
		for (i in 0 until 100) {
			val mapCell = randomMapCell.invoke()
			generateRoom(initialRoom, mapCell)
		}

		// Remove walls between rooms
		val temp = arrayListOf<Coordinate>()
		for (i in 0..3) {
			outerWalls[i]?.let { walls -> temp.addAll(walls) }
		}
		temp.asSequence()
				.groupBy { it.first * 1000 + it.second }
				.filter { it.value.size > 1 }
				.keys
				.forEach {
					GameController.changeObjectTile(it / 1000, it % 1000, TileRepo.emptyTile)
				}
	}


	private fun newRoom(): Room {
		return roomRepo.rooms.first()
	}

	private fun generateRoom(room: Room, mapCell: Coordinate) {
		val mapArea = cellsToRect(listOf(mapCell))
		val walls = placeRoom(room, mapArea)

		for (j in 0..3) {
			walls[j]?.let { newWalls -> outerWalls[j]?.addAll(newWalls) }
		}

		if (room.outerWalls[0] != null) {
			addToCandidates(mapCell.first to mapCell.second - 1)
		}
		if (room.outerWalls[1] != null) {
			addToCandidates(mapCell.first + 1 to mapCell.second)
		}
		if (room.outerWalls[2] != null) {
			addToCandidates(mapCell.first to mapCell.second + 1)
		}
		if (room.outerWalls[3] != null) {
			addToCandidates(mapCell.first - 1 to mapCell.second)
		}
	}

	private fun addToCandidates(coordinate: Coordinate) {
		mapAvailability.firstOrNull { it.first == coordinate.first && it.second == coordinate.second }?.let {
			mapCellCandidates.add(it)
			mapAvailability.remove(it)
		}
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

		for (w in 0 until room.width) {
			for (h in 0 until room.height) {
				val tileObject = room.objectTiles[room.objects[w][h].toString()]!!
				if (tileObject != TileRepo.voidTile) {
					changeTile(mapX + w,
							mapY + h,
							room.floorTiles[room.floor[w][h].toString()]!!,
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

}