package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.EnemyController
import ru.meatgames.tomb.domain.LevelMap
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import timber.log.Timber
import javax.inject.Inject

class PlaygroundMapGenerator @Inject constructor(
    roomsData: RoomsData,
    private val enemyController: EnemyController,
) : MapGenerator {
    
    private val rooms: List<Room> = roomsData.rooms
    private val floorMapping: List<FloorTileMapping> = roomsData.floorMapping
    private val objectMapping: List<ObjectTileMapping> = roomsData.objectMapping
    
    private val outerWallsPool: MutableSet<Pair<Int, Int>> = mutableSetOf()
    
    override fun generateMap(
        map: LevelMap,
    ): MapConfiguration {
        val initialRoomPositionX = 10
        val initialRoomPositionY = 3
        val initialRoom = rooms.first { it.name == "Playground" }
        
        map.clearMap()
        
        map.placeRoom(
            x = initialRoomPositionX,
            y = initialRoomPositionY,
            room = initialRoom,
        )
        
        enemyController.placeEnemy(
            enemyType = EnemyType.Skeleton,
            coordinates = initialRoomPositionX + initialRoom.width / 2 to initialRoomPositionY + 2,
            levelMap = map,
        )
        
        return MapConfiguration(
            mapWidth = map.width,
            mapHeight = map.height,
            startCoordinates = initialRoomPositionX + initialRoom.width / 2 to initialRoomPositionY + initialRoom.height / 2,
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
    }
    
    private fun EnemyController.placeEnemy(
        enemyType: EnemyType,
        coordinates: Coordinates,
        levelMap: LevelMap,
    ) {
        val tile = levelMap.getTile(coordinates.first, coordinates.second) ?: let {
            Timber.d("Enemy type $enemyType at $coordinates didn't spawn - incorrect coordinates")
            return
        }
        
        if (tile.objectEntityTile != null) {
            Timber.d("Enemy type $enemyType at $coordinates didn't spawn - space was blocked")
            return
        }
        
        addEnemy(
            coordinates = coordinates,
            enemy = Enemy(
                type = enemyType,
                position = coordinates,
            )
        )
    }
    
    private fun Char.toFloorEntity(): FloorEntityTile = floorMapping.first {
        it.symbol == this@toFloorEntity.toString()
    }.entity
    
    private fun Char.toObjectEntity(): ObjectEntityTile? = objectMapping.first {
        it.symbol == this@toObjectEntity.toString()
    }.entity
    
}
