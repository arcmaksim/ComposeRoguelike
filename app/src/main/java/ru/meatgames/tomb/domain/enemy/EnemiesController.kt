package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.resolvedOffset
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
    
    override fun getEnemy(
        enemyId: EnemyId,
    ): Enemy? = enemies[enemyId]
    
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
    
    override fun moveEnemy(
        enemyId: EnemyId,
        direction: Direction,
    ): Boolean {
        val enemy = enemies[enemyId] ?: return false
        val newPosition = (enemy.position + direction.resolvedOffset).toCoordinates()
        
        if (enemyMapping[newPosition] == null) {
            enemyMapping.remove(enemy.position.toCoordinates())
            enemyMapping[newPosition] = enemyId
            enemies[enemyId] = enemy.copy(position = newPosition.toPositionComponent())
            return true
        }
        
        return false
    }
    
    override fun getEnemies(): List<Enemy> = enemies.values.toList()
    
    override fun clearEnemies() {
        enemyMapping.clear()
        enemies.clear()
    }
    
    override fun tryToInflictDamage(
        coordinates: Coordinates,
        damage: Int,
    ): Boolean {
        val enemyId = enemyMapping[coordinates] ?: return false
        val enemy = enemies[enemyId] ?: let {
            enemyMapping.remove(coordinates)
            return false
        }
        
        val updatedComponent = enemy.health.updateHealth(-damage)
        if (updatedComponent.isDepleted) {
            enemyMapping.remove(coordinates)
            enemies.remove(enemyId)
            return true
        }
        
        enemies[enemyId] = enemy.copy(health = updatedComponent)
        return true
    }
}

// write interface
interface EnemiesController {
    
    fun addEnemy(
        coordinates: Coordinates,
        enemy: Enemy,
    )
    
    fun moveEnemy(
        enemyId: EnemyId,
        direction: Direction,
    ): Boolean
    
}

// readonly interface
interface EnemiesHolder {
    
    fun getEnemy(
        coordinates: Coordinates,
    ): Enemy?
    
    fun getEnemy(
        enemyId: EnemyId,
    ): Enemy?
    
    fun tryToInflictDamage(
        coordinates: Coordinates,
        damage: Int,
    ): Boolean
    
    fun getEnemies(): List<Enemy>
    
    fun clearEnemies()
    
}
