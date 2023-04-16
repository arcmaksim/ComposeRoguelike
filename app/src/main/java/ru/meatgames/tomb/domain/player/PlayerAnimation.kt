package ru.meatgames.tomb.domain.player

import ru.meatgames.tomb.Direction

sealed class PlayerAnimation {

    object Shake : PlayerAnimation()

    data class Move(
        val direction: Direction,
    ) : PlayerAnimation()
    
    data class Attack(
        val direction: Direction,
    ) : PlayerAnimation()

}

val PlayerAnimation?.isStateless: Boolean
    get() = when (this) {
        is PlayerAnimation.Shake,
        is PlayerAnimation.Attack -> true
        else -> false
    }
