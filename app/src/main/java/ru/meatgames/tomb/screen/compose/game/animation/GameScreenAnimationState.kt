package ru.meatgames.tomb.screen.compose.game.animation

import ru.meatgames.tomb.Direction

sealed class GameScreenAnimationState {

    class Shake : GameScreenAnimationState()

    class Scroll(
        val direction: Direction,
    ) : GameScreenAnimationState()

}

val GameScreenAnimationState?.isStateless: Boolean
    get() = when (this) {
        is GameScreenAnimationState.Shake -> true
        else -> false
    }
