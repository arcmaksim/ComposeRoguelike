package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction

sealed class PlayerAnimationState {

    class NoAnimation : PlayerAnimationState()

    class Shake : PlayerAnimationState()

    class Scroll(
        val direction: Direction,
    ) : PlayerAnimationState()

}
