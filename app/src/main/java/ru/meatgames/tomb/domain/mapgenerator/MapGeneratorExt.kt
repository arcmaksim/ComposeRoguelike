package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.EnemiesController
import ru.meatgames.tomb.domain.LevelMap
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.enemy.EnemyType
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
        enemy = Enemy(
            type = enemyType,
            position = coordinates,
        )
    )
}
