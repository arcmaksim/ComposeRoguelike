package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction

sealed class PlayerAnimationState {

    class Shake : PlayerAnimationState()

    class Scroll(
        val direction: Direction,
    ) : PlayerAnimationState()

}

val PlayerAnimationState?.isStateless: Boolean
    get() = when (this) {
        is PlayerAnimationState.Shake -> true
        else -> false
    }
