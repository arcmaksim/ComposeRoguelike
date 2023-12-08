package ru.meatgames.tomb.screen.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.screen.game.animation.EnemyAnimationEvent

enum class GameScreenEvent {
    NavigateToWinScreen,
    NavigateToInventory,
    NavigateToCharacterSheet,
}

/**
 * Class to represent state fot the [GameScreen].
 *
 * @param mapState - streamed part of the map (either Loading or Ready)
 */
data class GameScreenState(
    val mapState: MapScreenState = MapScreenState.Loading,
)

interface GameScreenNavigator {
    
    fun onNewMapRequest()
    
    fun navigateToInventory()
    
    fun navigateToCharacterSheet()
    
    fun showDialog()
    
}

interface GameScreenInteractionController {
    
    fun processCharacterMoveInput(direction: Direction)
    
    fun closeInteractionMenu()
    
    fun itemSelected(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    )
    
    fun skipTurn()
    
    // Move to the another interface because it's an animation callback and not an interaction
    fun onEnemyAnimationEvent(
        event: EnemyAnimationEvent,
    )
    
}
