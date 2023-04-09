package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.compose.game.animation.EnemiesAnimationState
import ru.meatgames.tomb.screen.compose.game.animation.PlayerAnimationState

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
 * @param interactionState - current player interaction state
 */
data class GameScreenState(
    val mapState: MapScreenController.MapScreenState = MapScreenController.MapScreenState.Loading,
    val playerAnimation: PlayerAnimationState? = null,
    val enemiesAnimations: List<Pair<EnemyId, EnemiesAnimationState>>? = null,
    val interactionState: PlayerInteractionState? = null,
)

interface GameScreenNavigator {
    
    fun onNewMapRequest()
    
    fun navigateToInventory()
    
    fun navigateToCharacterSheet()
    
}

interface GameScreenInteractionController {
    
    suspend fun finishPlayerAnimation()
    
    fun processCharacterMoveInput(direction: Direction)
    
    fun closeInteractionMenu()
    
    fun itemSelected(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    )
    
    suspend fun finishEnemiesAnimation()
    
}
