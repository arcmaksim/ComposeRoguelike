package ru.meatgames.tomb.presentation.player

import ru.meatgames.tomb.Direction

sealed class PlayerAnimation {
    
    data object Shake : PlayerAnimation()

    data class Move(
        val direction: Direction,
    ) : PlayerAnimation()
    
    data class Attack(
        val direction: Direction,
    ) : PlayerAnimation()
    
    // Needed to trigger game state change when there is no character animation
    data object None : PlayerAnimation()

}

val PlayerAnimation?.updatesScreenSpaceTiles: Boolean
    get() = when (this) {
        is PlayerAnimation.Shake,
        is PlayerAnimation.Attack -> false
        else -> true
    }
