package ru.meatgames.tomb.screen.compose.game.animation

import ru.meatgames.tomb.Direction

sealed class EnemiesAnimationState {

    data class Move(
        val direction: Direction,
    ) : EnemiesAnimationState()
    
    data class Attack(
        val direction: Direction,
    ) : EnemiesAnimationState()

}
