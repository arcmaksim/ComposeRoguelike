package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.screen.compose.game.MapTile
import javax.inject.Inject
import kotlin.random.Random

class SimpleMapGenerator @Inject constructor(
    roomsData: RoomsData,
) {

    private val maxRoomsAttempts = 25
    private val maxRoomPlacementAttempts = 5
    private val random = Random(System.currentTimeMillis())

    private val rooms: List<Room> = roomsData.rooms
    private val floorMapping: List<FloorTileMapping> = roomsData.floorMapping
    private val objectMapping: List<ObjectTileMapping> = roomsData.objectMapping

    private val outerWallsPool: MutableSet<Pair<Int, Int>> = mutableSetOf()

    fun generateMap(
        map: LevelMap,
    ): GeneratedMapConfiguration {
        val initialRoomPositionX = 10
        val initialRoomPositionY = 3
        val initialRoom = rooms.first()

        map.clearMap()

        map.placeRoom(
            x = initialRoomPositionX,
            y = initialRoomPositionY,
            room = initialRoom,
        )

        roomLoop@ for (i in 0 until maxRoomsAttempts) {
            log("-----------------------------------------")
            log("Attempting to place ${i + 1} room of $maxRoomsAttempts")
            val room = rooms.random(random)

            log("Outer walls pool - $outerWallsPool")

            for (roomPlacementAttempt in 0 until maxRoomPlacementAttempts) {
                val (randomOuterWall, direction) = map.getRandomOuterWall(random) ?: break@roomLoop

                log("Attempt ${roomPlacementAttempt + 1} - selected outer wall ${randomOuterWall.first} ${randomOuterWall.second}")
                log("Direction was resolved - $direction")

                val (mapX, mapY) = when (direction) {
                    Direction.Top -> randomOuterWall.first - 1 to randomOuterWall.second - (room.height - 1)
                    Direction.Bottom -> randomOuterWall.first - 1 to randomOuterWall.second
                    Direction.Left -> randomOuterWall.first - (room.width - 1) to randomOuterWall.second - 1
                    Direction.Right -> randomOuterWall.first to randomOuterWall.second - 1
                }

                val isZoneEmpty = map.checkZone(
                    mapX = mapX,
                    mapY = mapY,
                    roomWidth = room.width,
                    roomHeight = room.height,
                )

                if (!isZoneEmpty) continue

                map.placeRoom(mapX, mapY, room)
                map.updateSingleTile(
                    x = randomOuterWall.first,
                    y = randomOuterWall.second,
                ) {
                    copy(
                        objectEntityTile = ObjectEntityTile.DoorClosed,
                    )
                }
                log("Placed door at ${randomOuterWall.first} ${randomOuterWall.second}")
                outerWallsPool.remove(randomOuterWall)
                break
            }
        }

        return GeneratedMapConfiguration(
            mapWidth = map.width,
            mapHeight = map.height,
            startingPositionX = initialRoomPositionX + 2,
            startingPositionY = initialRoomPositionY + 2,
        )
    }

    private fun LevelMap.getRandomOuterWall(
        random: Random = Random,
    ): Pair<Pair<Int, Int>, Direction>? {
        while (outerWallsPool.isNotEmpty()) {
            val wall = outerWallsPool.random(random)

            val topTile = getTile(wall.first, wall.second - 1)
            val bottomTile = getTile(wall.first, wall.second + 1)
            val leftTile = getTile(wall.first - 1, wall.second)
            val rightTile = getTile(wall.first + 1, wall.second)

            when {
                topTile.isEmpty && bottomTile.isWall && leftTile.isWall && rightTile.isWall -> Direction.Bottom
                bottomTile.isEmpty && leftTile.isWall && rightTile.isWall && topTile.isWall -> Direction.Top
                leftTile.isEmpty && rightTile.isWall && topTile.isWall && bottomTile.isWall -> Direction.Right
                rightTile.isEmpty && topTile.isWall && bottomTile.isWall && leftTile.isWall -> Direction.Left
                else -> null
            }?.let {
                return wall to it
            }

            outerWallsPool.remove(wall)
        }

        return null
    }

    private fun LevelMap.clearMap() {
        updateBatch {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    updateSingleTile(
                        x = x,
                        y = y,
                    ) {
                        copy(
                            floorEntityTile = FloorEntityTile.Floor,
                            objectEntityTile = ObjectEntityTile.Wall,
                        )
                    }
                }
            }
        }
        outerWallsPool.clear()
    }

    private fun LevelMap.placeRoom(
        x: Int,
        y: Int,
        room: Room,
    ) {
        updateBatch {
            for (i in 0 until room.width * room.height) {
                val xOffset = i % room.width
                val yOffset = i / room.width

                updateSingleTile(
                    x = x + xOffset,
                    y = y + yOffset,
                ) {
                    copy(
                        floorEntityTile = room.floor[i].toFloorEntity(),
                        objectEntityTile = room.objects[i].toObjectEntity(),
                    )
                }
            }
        }

        for (wall in room.outerWalls) {
            outerWallsPool.add(x + wall.first to y + wall.second)
        }

        log("Placed room at $x $y with dimensions ${room.width} x ${room.height}")
    }

    private fun Char.toFloorEntity(): FloorEntityTile = floorMapping.first {
        it.symbol == this@toFloorEntity.toString()
    }.entity

    private fun Char.toObjectEntity(): ObjectEntityTile? = objectMapping.first {
        it.symbol == this@toObjectEntity.toString()
    }.entity

    private fun LevelMap.checkZone(
        mapX: Int,
        mapY: Int,
        roomWidth: Int,
        roomHeight: Int,
    ): Boolean {
        if (mapX + roomWidth > width - 3 || mapY + roomHeight > height - 3 || mapX < 2 || mapY < 2) return false
        log("Checking zone at $mapX $mapY dimensions $roomWidth x $roomHeight")
        for (x in mapX until mapX + roomWidth) {
            for (y in mapY until mapY + roomHeight) {
                val tile = getTile(x, y) ?: return false
                if (!tile.isWall) return false
            }
        }
        return true
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
        floor = floor.indices.map { index ->
            floor[(index / height) + (height - 1 - index % height) * width]
        }.fold("") { acc, item -> acc + item },
        objects = objects.indices.map { index ->
            objects[(index / height) + (height - 1 - index % height) * width]
        }.fold("") { acc, item -> acc + item },
    )

    private fun Room.rotate270(): Room = copy(
        width = height,
        height = width,
        floor = floor.indices.map { index ->
            floor[width - 1 + (index % height) * width - index / height]
        }.fold("") { acc, item -> acc + item },
        objects = objects.indices.map { index ->
            objects[width - 1 + (index % height) * width - index / height]
        }.fold("") { acc, item -> acc + item },
    )

    private fun Room.rotate180(): Room = copy(
        floor = floor.reversed(),
        objects = objects.reversed(),
    )

    private fun log(
        message: String,
    ) = logMessage(
        tag = "MapGeneration",
        message = message,
    )

}

data class GeneratedMapConfiguration(
    val mapWidth: Int,
    val mapHeight: Int,
    val startingPositionX: Int,
    val startingPositionY: Int,
)

private val MapTile?.isWall: Boolean
    get() {
        this ?: return false
        return objectEntityTile == ObjectEntityTile.Wall
    }

private val MapTile?.isEmpty: Boolean
    get() {
        this ?: return false
        return objectEntityTile == null
    }
