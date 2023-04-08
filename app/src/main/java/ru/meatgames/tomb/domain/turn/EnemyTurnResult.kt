package ru.meatgames.tomb.domain.turn

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.enemy.EnemyId

sealed class EnemyTurnResult {
    
    data class Attack(
        val enemyId: EnemyId,
        val direction: Direction,
        val amount: Int,
    ) : EnemyTurnResult()
    
    data class Move(
        val enemyId: EnemyId,
        val direction: Direction,
    ) : EnemyTurnResult()
    
}
