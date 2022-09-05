package ru.meatgames.tomb

import ru.meatgames.tomb.db.RoomDBClass
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.room.Room
import ru.meatgames.tomb.new_models.room.RoomRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.screen.compose.game.GameMapTile
import timber.log.Timber
import kotlin.random.Random

class NewMapGenerator2(
    private val tileRepo: TileRepo,
    private val roomRepo: RoomRepo,
    private val mapWidth: Int = 96,
    private val mapHeight: Int = 96,
) {

    private val mMaxRooms = 70

    private var placedRoomsData: Array<RoomDBClass?> = arrayOfNulls(mMaxRooms)

    private fun NewMapController2.checkZone(
        mapX: Int,
        mapY: Int,
        width: Int,
        height: Int,
    ): Boolean {
        if (mapX + width > mapWidth - 3 || mapY + height > mapHeight - 3 || mapX < 2 || mapY < 2) return false
        Timber.d("Checking zone at $mapX $mapY dimensions $width x $height")
        for (x in mapX until mapX + width) {
            for (y in mapY until mapY + height) {
                val tile = getTile(x, y) ?: return false
                if (!tile.isWall) return false
            }
        }
        return true
    }

    private val maxRooms = 25
    private val maxRoomAttempts = 25

    private var outerWallsPool: MutableSet<Pair<Int, Int>> = mutableSetOf()

    fun generateMap(): NewMapConfig {
        val mapController = NewMapController2(mapWidth, mapHeight)

        mapController.clearMap()

        val initialRoomPositionX = 10
        val initialRoomPositionY = 3
        val initialRoom = roomRepo.rooms.first()

        mapController.placeRoom(
            initialRoomPositionX,
            initialRoomPositionY,
            initialRoom,
        )

        val random = Random(System.currentTimeMillis())

        roomLoop@ for (i in 0 until maxRooms) {
            Timber.d("-----------------------------------------")
            Timber.d("Attempting to place ${i + 1} room of $maxRooms")
            val room = roomRepo.rooms.random(random).rotate()

            Timber.d("Outer walls pool - $outerWallsPool")

            for (roomPlacementAttempt in 0 until maxRoomAttempts) {
                val randomOuterWall = mapController.getRandomOuterWall(random) ?: break@roomLoop

                Timber.d("Attempt ${roomPlacementAttempt + 1} - selected outer wall ${randomOuterWall.first} ${randomOuterWall.second}")

                val topTile =
                    mapController.getTile(randomOuterWall.first, randomOuterWall.second - 1)
                val bottomTile =
                    mapController.getTile(randomOuterWall.first, randomOuterWall.second + 1)
                val leftTile =
                    mapController.getTile(randomOuterWall.first - 1, randomOuterWall.second)
                val rightTile =
                    mapController.getTile(randomOuterWall.first + 1, randomOuterWall.second)

                val resolvedDirection: Direction? = when {
                    topTile.isEmpty && bottomTile.isWall && leftTile.isWall && rightTile.isWall -> Direction.Bottom
                    bottomTile.isEmpty && leftTile.isWall && rightTile.isWall && topTile.isWall -> Direction.Top
                    leftTile.isEmpty && rightTile.isWall && topTile.isWall && bottomTile.isWall -> Direction.Right
                    rightTile.isEmpty && topTile.isWall && bottomTile.isWall && leftTile.isWall -> Direction.Left
                    else -> null
                }

                resolvedDirection ?: continue

                Timber.d("Direction was resolved - $resolvedDirection")

                val (mapX, mapY) = when (resolvedDirection) {
                    Direction.Top -> randomOuterWall.first - 1 to randomOuterWall.second - (room.height - 1)
                    Direction.Bottom -> randomOuterWall.first - 1 to randomOuterWall.second
                    Direction.Left -> randomOuterWall.first - (room.width - 1) to randomOuterWall.second - 1
                    Direction.Right -> randomOuterWall.first to randomOuterWall.second - 1
                    else -> throw IllegalStateException()
                }

                val result = mapController.checkZone(
                    mapX = mapX,
                    mapY = mapY,
                    width = room.width,
                    height = room.height,
                )
                if (result) {
                    mapController.placeRoom(mapX, mapY, room)
                    mapController.changeObjectTile(
                        randomOuterWall.first,
                        randomOuterWall.second,
                        tileRepo.getTile("door_closed"),
                    )
                    Timber.d("Placed door at ${randomOuterWall.first} ${randomOuterWall.second}")
                    outerWallsPool.remove(randomOuterWall)
                    break
                }
            }
        }

        return NewMapConfig(
            startingPositionX = initialRoomPositionX + 2,
            startingPositionY = initialRoomPositionY + 2,
            mapController = mapController,
        )
    }

    private fun NewMapController2.getRandomOuterWall(
        random: Random = Random,
    ): Pair<Int, Int>? {
        while (outerWallsPool.isNotEmpty()) {
            val randomOuterWall = outerWallsPool.random(random)

            val topTile = getTile(randomOuterWall.first, randomOuterWall.second - 1)
            val bottomTile = getTile(randomOuterWall.first, randomOuterWall.second + 1)
            val leftTile = getTile(randomOuterWall.first - 1, randomOuterWall.second)
            val rightTile = getTile(randomOuterWall.first + 1, randomOuterWall.second)

            val isWalidOuterWall: Boolean = when {
                topTile.isEmpty && bottomTile.isWall && leftTile.isWall && rightTile.isWall -> true
                bottomTile.isEmpty && leftTile.isWall && rightTile.isWall && topTile.isWall -> true
                leftTile.isEmpty && rightTile.isWall && topTile.isWall && bottomTile.isWall -> true
                rightTile.isEmpty && topTile.isWall && bottomTile.isWall && leftTile.isWall -> true
                else -> false
            }

            if (!isWalidOuterWall) {
                outerWallsPool.remove(randomOuterWall)
            } else {
                return randomOuterWall
            }
        }

        return null
    }

    private fun NewMapController2.clearMap() {
        for (x in 0 until mapWidth) {
            for (y in 0 until mapHeight) {
                changeTile(
                    x,
                    y,
                    GameDataProvider.tiles.getTile("floor_0"),
                    GameDataProvider.tiles.getTile("wall_0"),
                )
            }
        }
        outerWallsPool.clear()
    }

    private fun NewMapController2.placeRoom(
        x: Int,
        y: Int,
        room: Room,
    ) {
        for (w in 0 until room.width) {
            for (h in 0 until room.height) {
                changeTile(
                    x + w,
                    y + h,
                    room.floorTiles[room.floor[w + h * room.width]]!!,
                    room.objectTiles[room.objects[w + h * room.width]]!!,
                )
            }
        }

        for (wall in room.outerWalls) {
            outerWallsPool.add(x + wall.first to y + wall.second)
        }

        Timber.d("Placed room at $x $y with dimensions ${room.width} x ${room.height}")
    }

    private fun NewMapController2.changeTile(
        mapX: Int,
        mapY: Int,
        floorTile: Tile,
        objectTile: Tile,
    ) {
        changeFloorTile(mapX, mapY, floorTile)
        changeObjectTile(mapX, mapY, objectTile)
    }

    private fun Room.verticallyMirrored(): Room = copy(
        floor = floor.reversed(),
        objects = objects.reversed(),
    )

    private fun Room.rotate(
        random: Random = Random,
    ): Room = when (random.nextInt(4)) {
        0 -> this
        1 -> rotate90()
        2 -> rotate180()
        else -> rotate270()
    }

    private fun Room.rotate90(): Room = copy(
        width = height,
        height = width,
        floor = List(floor.size) { index -> floor[(index / height) + (height - 1 - index % height) * width] },
        objects = List(objects.size) { index -> objects[(index / height) + (height - 1 - index % height) * width] },
    )

    private fun Room.rotate270(): Room = copy(
        width = height,
        height = width,
        floor = List(floor.size) { index -> floor[width - 1 + (index % height) * width - index / height] },
        objects = List(objects.size) { index -> objects[width - 1 + (index % height) * width - index / height] },
    )

    private fun Room.rotate180(): Room = copy(
        floor = floor.reversed(),
        objects = objects.reversed(),
    )

}

data class NewMapConfig(
    val startingPositionX: Int,
    val startingPositionY: Int,
    val mapController: NewMapController2,
)

private val GameMapTile?.isWall: Boolean
    get() = this?.`object`?.name == "wall_0"

private val GameMapTile?.isEmpty: Boolean
    get() = this?.`object` == null || this.`object`.name == "nothing" || this.`object`.name == "void"
