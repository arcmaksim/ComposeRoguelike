package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.enemy.EnemiesController
import ru.meatgames.tomb.domain.map.LevelMap
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.domain.enemy.produceEnemy
import timber.log.Timber

internal fun EnemiesController.placeEnemy(
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
        enemy = enemyType.produceEnemy(coordinates),
    )
}
