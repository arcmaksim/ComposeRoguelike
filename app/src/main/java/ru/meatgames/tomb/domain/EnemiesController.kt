package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.enemy.EnemyId
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnemiesControllerImpl @Inject constructor() : EnemiesController, EnemiesHolder {
    
    private val enemies = mutableMapOf<EnemyId, Enemy>()
    private val enemyMapping = mutableMapOf<Coordinates, EnemyId>()
    
    override fun getEnemy(
        coordinates: Coordinates,
    ): Enemy? = enemyMapping[coordinates]?.let { enemies[it] }
    
    override fun addEnemy(
        coordinates: Coordinates,
        enemy: Enemy,
    ) {
        if (enemyMapping[coordinates] != null) {
            Timber.d("Enemy with id ${enemy.id.id} at $coordinates didn't spawn - space was occupied")
            return
        }
        
        enemies[enemy.id] = enemy
        enemyMapping[coordinates] = enemy.id
    
        Timber.d("Enemy with id ${enemy.id.id} at $coordinates was spawn")
    }
    
    override fun clearEnemies() {
        enemyMapping.clear()
        enemies.clear()
    }
    
}

// write interface
interface EnemiesController {
    
    fun addEnemy(
        coordinates: Coordinates,
        enemy: Enemy,
    )
    
}

// readonly interface
interface EnemiesHolder {
    
    fun getEnemy(
        coordinates: Coordinates,
    ): Enemy?
    
    fun clearEnemies()
    
}
