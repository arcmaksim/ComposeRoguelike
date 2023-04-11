package ru.meatgames.tomb.screen.compose.game.animation

import ru.meatgames.tomb.Direction

sealed class PlayerAnimationState {

    object Shake : PlayerAnimationState()

    data class Move(
        val direction: Direction,
    ) : PlayerAnimationState()
    
    data class Attack(
        val direction: Direction,
    ) : PlayerAnimationState()

}

val PlayerAnimationState?.isStateless: Boolean
    get() = when (this) {
        is PlayerAnimationState.Shake,
        is PlayerAnimationState.Attack -> true
        else -> false
    }
