package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.room.domain.rotate180
import ru.meatgames.tomb.model.room.domain.rotateClockwise
import ru.meatgames.tomb.model.room.domain.rotateCounterclockwise
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import kotlin.random.Random

typealias Coordinates = Pair<Int, Int>
typealias ScreenSpaceCoordinates = Coordinates

class SimpleMapGenerator @Inject constructor(
    roomsData: RoomsData,
    private val itemsController: ItemsController,
) {
    
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
    
        Item("Initial item ${System.currentTimeMillis().toString().takeLast(5)}").placeItem(
            x = initialRoomPositionX + 2,
            y = initialRoomPositionY + 1,
        )
        
        map.generateRooms(
            maxRoomsAttempts = 25,
            maxRoomPlacementAttempts = 50,
        )
        
        map.placeItems(
            amount = 10,
            random = random,
        )
        
        return GeneratedMapConfiguration(
            mapWidth = map.width,
            mapHeight = map.height,
            startCoordinates = initialRoomPositionX + 2 to initialRoomPositionY + 2,
        )
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
    
    private fun LevelMap.generateRooms(
        maxRoomsAttempts: Int,
        maxRoomPlacementAttempts: Int,
    ) {
        roomLoop@ for (i in 0 until maxRoomsAttempts) {
            log("-----------------------------------------")
            log("Attempting to place ${i + 1} room of $maxRoomsAttempts")
            val room = rooms.random(random).rotate(random)
            log("Selected room ${room.name} - ${room.width}x${room.height}")
            
            log("Outer walls pool - $outerWallsPool")
            
            for (roomPlacementAttempt in 0 until maxRoomPlacementAttempts) {
                val (randomOuterWall, direction) = getRandomOuterWall(random) ?: break@roomLoop
                val (mapX, mapY) = room.findTile(
                    randomOuterWall = randomOuterWall,
                    direction = direction,
                    random = random,
                ) ?: continue
                
                log("Attempt ${roomPlacementAttempt + 1} - selected outer wall ${randomOuterWall.first} ${randomOuterWall.second}")
                log("mapX: $mapX, mapY: $mapY")
                log("Direction was resolved - $direction")
                
                val isZoneEmpty = checkZone(
                    mapX = mapX,
                    mapY = mapY,
                    roomWidth = room.width,
                    roomHeight = room.height,
                )
                
                if (!isZoneEmpty) continue
                
                placeRoom(mapX, mapY, room)
                updateSingleTile(
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
    }
    
    private fun LevelMap.getRandomOuterWall(
        random: Random = Random,
    ): Pair<Coordinates, Direction>? {
        val localOuterWallsPool = outerWallsPool.map { it.first to it.second }.toMutableSet()
        
        while (localOuterWallsPool.isNotEmpty()) {
            val wall = localOuterWallsPool.random(random)
            
            val topTile = getTile(wall.first, wall.second - 1)?.tile
            val bottomTile = getTile(wall.first, wall.second + 1)?.tile
            val leftTile = getTile(wall.first - 1, wall.second)?.tile
            val rightTile = getTile(wall.first + 1, wall.second)?.tile
            
            when {
                topTile.isEmpty && bottomTile.isWall && leftTile.isWall && rightTile.isWall -> Direction.Bottom
                bottomTile.isEmpty && leftTile.isWall && rightTile.isWall && topTile.isWall -> Direction.Top
                leftTile.isEmpty && rightTile.isWall && topTile.isWall && bottomTile.isWall -> Direction.Right
                rightTile.isEmpty && topTile.isWall && bottomTile.isWall && leftTile.isWall -> Direction.Left
                else -> null
            }?.let {
                return wall to it
            }
            
            localOuterWallsPool.remove(wall)
        }
        
        return null
    }
    
    private fun Room.findTile(
        randomOuterWall: Coordinates,
        direction: Direction,
        random: Random,
    ): Coordinates? {
        log("Resolving room wall for $name, at ${randomOuterWall.first} ${randomOuterWall.second} with $direction")
        
        val wall = when (direction) {
            Direction.Top -> outerWalls.filter { it.second == height - 1 }.randomOrNull(random)
            Direction.Bottom -> outerWalls.filter { it.second == 0 }.randomOrNull(random)
            Direction.Left -> outerWalls.filter { it.first == width - 1 }.randomOrNull(random)
            Direction.Right -> outerWalls.filter { it.first == 0 }.randomOrNull(random)
        } ?: return null
        
        return when (direction) {
            Direction.Top -> randomOuterWall.first - wall.first to randomOuterWall.second - wall.second
            Direction.Bottom -> randomOuterWall.first - wall.first to randomOuterWall.second
            Direction.Left -> randomOuterWall.first - wall.first to randomOuterWall.second - wall.second
            Direction.Right -> randomOuterWall.first to randomOuterWall.second - wall.second
        }
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
    
    private fun LevelMap.placeItems(
        amount: Int,
        random: Random,
    ) {
        for (i in 0 until amount) {
            while (true) {
                val x = random.nextInt(width)
                val y = random.nextInt(height)
                val tile = getTile(x, y)?.tile
                if (tile.isEmpty) {
                    Item("Item $i ${System.currentTimeMillis().toString().takeLast(5)}").placeItem(x, y)
                    break
                }
            }
        }
    }
    
    private fun Item.placeItem(
        x: Int,
        y: Int,
    ) {
        itemsController.addItem(
            coordinates = Coordinates(x, y),
            item = this,
        )
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
                val tile = getTile(x, y)?.tile ?: return false
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
        1 -> rotateClockwise()
        2 -> rotate180()
        else -> rotateCounterclockwise()
    }
    
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
    val startCoordinates: Coordinates,
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
