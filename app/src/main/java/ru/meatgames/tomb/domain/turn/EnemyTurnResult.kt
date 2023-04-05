package ru.meatgames.tomb.domain.turn

import ru.meatgames.tomb.Direction

sealed class EnemyTurnResult {
    
    data class Attack(
        val direction: Direction,
        val amount: Int,
    ) : EnemyTurnResult()
    
}
