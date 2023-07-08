package ru.meatgames.tomb.domain.enemy

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.AttackInstance
import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.resolvedOffset
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private typealias MutableState = Triple<
    MutableMap<EnemyId, Enemy>,
    MutableMap<Coordinates, EnemyId>,
    MutableMap<EnemyId, AttackInstance>
>

data class EnemiesState(
    val enemies: Map<EnemyId, Enemy> = emptyMap(),
    val enemiesMapping: Map<Coordinates, EnemyId> = emptyMap(),
    val attacks: Map<EnemyId, AttackInstance> = emptyMap(),
)

@Singleton
class EnemiesControllerImpl @Inject constructor() : EnemiesController, EnemiesHolder {
    
    private val _state = MutableStateFlow(EnemiesState())
    val state: StateFlow<EnemiesState> = _state
    
    private val mutableState: MutableState
        get() = with(state.value) {
            return Triple(
                enemies.toMutableMap(),
                enemiesMapping.toMutableMap(),
                attacks.toMutableMap(),
            )
        }
    
    override fun getEnemy(
        coordinates: Coordinates,
    ): Enemy? = with(state.value) {
        enemiesMapping[coordinates]?.let(enemies::get)
    }
    
    override fun getEnemy(
        enemyId: EnemyId,
    ): Enemy? = state.value.enemies[enemyId]
    
    override fun addEnemy(
        coordinates: Coordinates,
        enemy: Enemy,
    ) {
        val (enemies, enemyMapping, _) = mutableState
        
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
        val (enemies, enemyMapping, _) = mutableState
        
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
    
    override fun getEnemies(): List<Enemy> = state
        .value
        .enemies
        .values
        .toList()
    
    override fun clearEnemies() {
        _state.value = EnemiesState()
    }
    
    override fun tryToInflictDamage(
        coordinates: Coordinates,
        damage: Int,
    ): Boolean {
        val (enemies, enemyMapping, attacks) = mutableState
        
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
    
    fun reduceAttackCountdown() {
        val (_, _, attacks) = mutableState
        
        val attackInstances = attacks.values.toList()
        attacks.clear()
        
        attackInstances.forEach { attackInstance ->
            val updatedCountdown = attackInstance.countdown - 1
            if (updatedCountdown > 0) {
                attacks[attackInstance.enemyId] = attackInstance.copy(countdown = updatedCountdown)
            } else {
                tryToInflictDamage(attackInstance.target, attackInstance.attack.damage)
            }
        }
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

