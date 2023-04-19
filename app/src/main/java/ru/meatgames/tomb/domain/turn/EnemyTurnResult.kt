package ru.meatgames.tomb.domain.turn

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.enemy.EnemyId

sealed class EnemyTurnResult {
    
    abstract val enemyId: EnemyId
    abstract val position: Coordinates
    
    data class Attack(
        override val enemyId: EnemyId,
        override val position: Coordinates,
        val direction: Direction,
        val amount: Int,
    ) : EnemyTurnResult()
    
    data class Move(
        override val enemyId: EnemyId,
        override val position: Coordinates,
        val direction: Direction,
    ) : EnemyTurnResult()
    
}
