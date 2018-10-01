package ru.meatgames.tomb

import android.content.Context
import ru.meatgames.tomb.db.RoomDBClass
import ru.meatgames.tomb.new_models.map.MapTile
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.room.Room
import ru.meatgames.tomb.new_models.room.RoomRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.util.MapHelper
import java.util.*
import kotlin.collections.HashMap

class NewMapGenerator(context: Context) {

	private val mMaxRooms = 70

	private var placedRoomsData: Array<RoomDBClass?> = arrayOfNulls(mMaxRooms)
	private val roomRepo: RoomRepo = GameDataProvider.getRooms(context)

	var currentRoom: Array<IntArray>? = null
	val rnd: Random = Random()
	var m: Int = 0
	var n: Int = 0

	var possibleNextRoomX = 0
	var possibleNextRoomY = 0

	var xl: Int = 0
	var xr: Int = 0
	var yl: Int = 0
	var yr: Int = 0

	val outerWalls: HashMap<Int, ArrayList<Pair<Int, Int>>> = HashMap()

	val minAreaWidth: Int = 6
	val minAreaHeight: Int = 6
	val minAreaSquare: Int = 40
	val targetRoomRatio: Float = .75F


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
				&& !MapHelper.isWall(x, y - 1) xor !MapHelper.isWall(x,
				y + 1) xor (!MapHelper.isWall(x - 1, y) xor !MapHelper.isWall(x + 1, y))
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

	/*fun newZone(width: Int, height: Int, roomIndex: Int) {
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
	}*/

	fun newRoom(): Room {
		return roomRepo.rooms.first()
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

		val tileRepo = GameDataProvider.tiles
		for (x in 0 until MapHelper.mMapWidth) {
			for (y in 0 until MapHelper.mMapHeight) {
				changeTile(x, y, tileRepo.getTile("floor_0"), TileRepo.voidTile)
			}
		}

		var x2 = rnd.nextInt(MapHelper.mMapWidth / 2) + 16
		var y2 = rnd.nextInt(MapHelper.mMapHeight / 2) + 16

		var up: Boolean
		var down: Boolean
		var left: Boolean
		var right: Boolean

		GameController.initNewGame(x2, y2)

		val initialRoom = roomRepo.rooms.first()

		val random = Random()

		for (i in 0..9) {
			val direction = random.nextInt(4)
			val walls = initialRoom.outerWalls[direction] ?: emptyList<ArrayList<Pair<Int, Int>>>()
			if (walls.isNotEmpty()) {
				val randomWall = walls[random.nextInt(walls.size)]

				val roomCandidate = roomRepo.getRandomRoom()
				val targetDirection = (direction + 2) % 4
				val wallCandidates = roomCandidate.outerWalls[targetDirection] ?: emptyList<ArrayList<Pair<Int, Int>>>()
				if (wallCandidates.isEmpty()) continue

				val wallCandidate = wallCandidates[random.nextInt(wallCandidates.size)]

				// TODO: get real world coordinates of randomWall
				if (isArea(roomCandidate, direction, 0 to 0, 0 to 0)) {
					// TODO: place room
				}
			}
		}

		placeRoom(x2, y2, initialRoom)
	}

	// Maybe return array of tiles and just place its contents to map?
	private fun isArea(
			room: Room,
			direction: Int,
			mapCoordinates: Pair<Int, Int>,
			roomWallCoordinates: Pair<Int, Int>): Boolean {

		val array: Array<MapTile> = Array(room.width * room.height) { MapTile() }

		for (x in 0 until room.width) {
			for (y in 0 until room.height) {

			}
		}
		/**
		 * TODO: calculate fromX, fromY, toX, toY using mapCoordinates and roomWallCoordinates
		 * 		 populate array using map and room
		 * 		 if conflict appear just return null
		 */
		return false
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
		//newZone(lx, ly, n)
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

	private fun placeRoom(x: Int, y: Int, room: Room) {
		for (w in 0 until room.width) {
			for (h in 0 until room.height) {
				changeTile(x + w,
						y + h,
						room.floorTiles[room.floor[w][h].toString()]!!,
						room.objectTiles[room.objects[w][h].toString()]!!)
			}
		}
	}

	private fun changeTile(mapX: Int, mapY: Int, floorTile: Tile, objectTile: Tile) {
		GameController.changeFloorTile(mapX, mapY, floorTile)
		GameController.changeObjectTile(mapX, mapY, objectTile)
	}

}