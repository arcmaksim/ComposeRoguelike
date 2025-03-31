package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.domain.map.LevelMap
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject

class MechanicsPlaygroundMapGenerator @Inject constructor(
    roomsData: RoomsData,
) : MapGenerator {

    private val rooms: Map<String, Room> = roomsData.rooms.associateBy { it.name }
    private val floorMapping: List<FloorTileMapping> = roomsData.floorMapping
    private val objectMapping: List<ObjectTileMapping> = roomsData.objectMapping

    private val outerWallsPool: MutableSet<Pair<Int, Int>> = mutableSetOf()

    override fun generateMap(
        map: LevelMap,
    ): MapConfiguration {
        FeatureToggles.themeOverride = "Caves"

        val initialRoom = rooms["Circle"]!!
        val initialRoomPositionX = map.width / 2 - initialRoom.width / 2
        val initialRoomPositionY = map.height / 2 - initialRoom.height / 2

        map.clearMap()

        map.placeRoom(
            x = initialRoomPositionX,
            y = initialRoomPositionY,
            room = initialRoom,
        )

        return MapConfiguration(
            mapWidth = map.width,
            mapHeight = map.height,
            startCoordinates = initialRoomPositionX + initialRoom.width / 2 to initialRoomPositionY + initialRoom.height / 2,
        )
    }

    private fun LevelMap.clearMap() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                updateSingleTile(
                    x = x,
                    y = y,
                    update = {
                        copy(
                            floorEntityTile = FloorEntityTile.Floor,
                            objectEntityTile = ObjectEntityTile.Wall,
                        )
                    },
                )
            }
        }
        outerWallsPool.clear()
    }

    private fun LevelMap.placeRoom(
        x: Int,
        y: Int,
        room: Room,
    ) {
        for (i in 0 until room.width * room.height) {
            val xOffset = i % room.width
            val yOffset = i / room.width

            updateSingleTile(
                x = x + xOffset,
                y = y + yOffset,
                update = {
                    copy(
                        floorEntityTile = room.floor[i].toFloorEntity(),
                        objectEntityTile = room.objects[i].toObjectEntity(),
                    )
                },
            )
        }

        for (wall in room.outerWalls) {
            outerWallsPool.add(x + wall.first to y + wall.second)
        }
    }

    private fun Char.toFloorEntity(): FloorEntityTile = floorMapping.first {
        it.symbol == this@toFloorEntity.toString()
    }.entity

    private fun Char.toObjectEntity(): ObjectEntityTile? = objectMapping.first {
        it.symbol == this@toObjectEntity.toString()
    }.entity

}
