package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.compose.game.animation.GameScreenAnimationState

enum class GameScreenEvent {
    NavigateToWinScreen,
    NavigateToInventory,
    NavigateToCharacterSheet,
}

/**
 * Class to represent state fot the [GameScreen].
 *
 * @param mapState - streamed part of the map (either Loading or Ready)
 * @param playerAnimation - current player animation to be played
 * @param previousMoveDirection - last movement direction to resolve scrolling animation
 * @param interactionState - current player interaction state
 */
data class GameScreenState(
    val mapState: MapScreenController.MapScreenState = MapScreenController.MapScreenState.Loading,
    val playerAnimation: GameScreenAnimationState? = null,
    val previousMoveDirection: Direction? = null,
    val interactionState: GameScreenInteractionState? = null,
)

interface GameScreenNavigator {
    
    fun onNewMapRequest()
    
    fun navigateToInventory()
    
    fun navigateToCharacterSheet()
    
}

interface GameScreenInteractionController {
    
    fun processCharacterMoveInput(direction: Direction)
    
    fun closeInteractionMenu()
    
    fun itemSelected(
        coordinates: Coordinates,
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    )
    
}
