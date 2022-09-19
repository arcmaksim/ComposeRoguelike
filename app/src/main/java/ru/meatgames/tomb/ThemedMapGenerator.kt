package ru.meatgames.tomb

import ru.meatgames.tomb.new_models.room.Room
import ru.meatgames.tomb.new_models.themed.data.ThemedRoomsRepository
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoom
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoomSymbolMapping
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTile
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurpose
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurposeDefinition
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTileset
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
import timber.log.Timber
import kotlin.random.Random

class ThemedMapGenerator(
    roomRepo: ThemedRoomsRepository,
    private val mapWidth: Int = 96,
    private val mapHeight: Int = 96,
) {

    private val tileset: ThemedTileset
    private val tiles: List<ThemedTilePurposeDefinition>
    private val rooms: List<ThemedRoom>
    private val mapping: List<ThemedRoomSymbolMapping>
    private val random = Random(System.currentTimeMillis())

    init {
        val themedRoomsConfig = roomRepo.loadData()
        tileset = themedRoomsConfig.tilesets.random(random)
        tiles = themedRoomsConfig.tiles
        rooms = themedRoomsConfig.rooms
        mapping = themedRoomsConfig.symbolMappings
    }

    private fun ThemedMapController.checkZone(
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

    fun generateMap(): ThemedNewMapConfig {
        val mapController = ThemedMapController(mapWidth, mapHeight)

        mapController.clearMap()

        val initialRoomPositionX = 10
        val initialRoomPositionY = 3
        val initialRoom = rooms.first()

        mapController.placeRoom(
            initialRoomPositionX,
            initialRoomPositionY,
            initialRoom,
        )

        val random = Random(System.currentTimeMillis())

        roomLoop@ for (i in 0 until this.maxRooms) {
            Timber.d("-----------------------------------------")
            Timber.d("Attempting to place ${i + 1} room of ${this.maxRooms}")
            val room = rooms.random(random)

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
                        ThemedTile
                        //tileRepo.getTile("door_closed"),
                    )
                    Timber.d("Placed empty tile at ${randomOuterWall.first} ${randomOuterWall.second}")
                    outerWallsPool.remove(randomOuterWall)
                    break
                }
            }
        }

        return ThemedNewMapConfig(
            startingPositionX = initialRoomPositionX + 2,
            startingPositionY = initialRoomPositionY + 2,
            mapController = mapController,
        )
    }

    private fun ThemedMapController.getRandomOuterWall(
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

    private fun ThemedMapController.clearMap() {
        for (x in 0 until mapWidth) {
            for (y in 0 until mapHeight) {
                changeTile(
                    mapX = x,
                    mapY = y,
                    floorTile = tiles.first { it.purpose == ThemedTilePurpose.FloorVariant1 }
                        .toThemedTile(tileset),
                    objectTile = tiles.first { it.purpose == ThemedTilePurpose.Wall }
                        .toThemedTile(tileset),
                )
            }
        }
        outerWallsPool.clear()
    }

    private fun ThemedMapController.placeRoom(
        x: Int,
        y: Int,
        room: ThemedRoom,
    ) {
        for (w in 0 until room.width) {
            for (h in 0 until room.height) {
                changeTile(
                    mapX = x + w,
                    mapY = y + h,
                    floorTile = room.floor[w + h * room.width].toThemedTile(
                        tileset = tileset,
                        tiles = tiles,
                        symbolMapping = mapping,
                    ),
                    objectTile = room.objects[w + h * room.width].toThemedTile(
                        tileset = tileset,
                        tiles = tiles,
                        symbolMapping = mapping,
                    ),
                )
            }
        }

        for (wall in room.outerWalls) {
            outerWallsPool.add(x + wall.first to y + wall.second)
        }

        Timber.d("Placed room at $x $y with dimensions ${room.width} x ${room.height}")
    }

    private fun ThemedMapController.changeTile(
        mapX: Int,
        mapY: Int,
        floorTile: ThemedTile,
        objectTile: ThemedTile,
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

private fun ThemedTilePurposeDefinition.toThemedTile(
    themedTileset: ThemedTileset,
): ThemedTile {
    val (isPassable, isTransparent, isUsable) = when (purpose) {
        ThemedTilePurpose.Empty, ThemedTilePurpose.FloorVariant1,
        ThemedTilePurpose.FloorVariant2, ThemedTilePurpose.FloorVariant3,
        ThemedTilePurpose.FloorVariant4 -> Triple(true, true, false)
        ThemedTilePurpose.StairsDown -> Triple(false, true, true)
        ThemedTilePurpose.StairsUp -> Triple(false, false, true)
        ThemedTilePurpose.Wall, ThemedTilePurpose.WallCracked,
        ThemedTilePurpose.WallDamaged, ThemedTilePurpose.WallCrackedVertical,
        ThemedTilePurpose.WallCrackedHorizontal -> Triple(false, false, true)
    }

    return ThemedTile(
        theme = themedTileset,
        purposeDefinition = this,
        isPassable = isPassable,
        isUsable = isUsable,
        isTransparent = isTransparent,
    )
}

private fun Char.toThemedTile(
    tileset: ThemedTileset,
    tiles: List<ThemedTilePurposeDefinition>,
    symbolMapping: List<ThemedRoomSymbolMapping>,
): ThemedTile {
    val mapping = symbolMapping.first { it.symbol == this }
    val purposeDefinition = tiles.first { it.purpose == mapping.purpose }
    return purposeDefinition.toThemedTile(tileset)
}

data class ThemedNewMapConfig(
    val startingPositionX: Int,
    val startingPositionY: Int,
    val mapController: ThemedMapController,
)

private val ThemedGameMapTile?.isWall: Boolean
    get() = this?.`object`?.purposeDefinition?.purpose == ThemedTilePurpose.Wall

private val ThemedGameMapTile?.isEmpty: Boolean
    get() = this?.`object` == null || this.`object`.purposeDefinition.purpose == ThemedTilePurpose.Empty
