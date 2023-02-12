package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerAnimationState

/**
 * Class to represent state fot the [GameScreen].
 *
 * @param mapState - streamed part of the map (either Loading or Ready)
 * @param playerAnimation - current player animation to be played
 * @param previousMoveDirection - last movement direction to resolve scrolling animation
 */
data class GameScreenState(
    val mapState: MapScreenController.MapScreenState = MapScreenController.MapScreenState.Loading,
    val playerAnimation: PlayerAnimationState = PlayerAnimationState.NoAnimation(),
    val previousMoveDirection: Direction? = null,
)
