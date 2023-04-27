package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.map.EnemiesAnimations
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.domain.player.PlayerAnimation

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
 * @param enemiesAnimations - current enemies animations to be played
 */
data class GameScreenState(
    val mapState: MapScreenState = MapScreenState.Loading,
    val playerAnimation: PlayerAnimation? = null,
    val enemiesAnimations: EnemiesAnimations? = null,
)

interface GameScreenNavigator {
    
    fun onNewMapRequest()
    
    fun navigateToInventory()
    
    fun navigateToCharacterSheet()
    
    fun showDialog()
    
}

interface GameScreenInteractionController {
    
    fun finishPlayerAnimation()
    
    fun processCharacterMoveInput(direction: Direction)
    
    fun closeInteractionMenu()
    
    fun itemSelected(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    )
    
    fun finishEnemiesAnimation()
    
    fun skipTurn()
    
}
