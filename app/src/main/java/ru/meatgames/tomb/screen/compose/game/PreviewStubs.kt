package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.compose.game.animation.EnemyAnimationEvent

internal val navigatorPreviewStub = object : GameScreenNavigator {
    override fun onNewMapRequest() = Unit
    
    override fun navigateToInventory() = Unit
    
    override fun navigateToCharacterSheet() = Unit
    
    override fun showDialog() = Unit
}

internal val interactionControllerPreviewStub = object : GameScreenInteractionController {
    override fun finishPlayerAnimation() = Unit
    override fun finishEnemiesAnimation() = Unit
    override fun processCharacterMoveInput(direction: Direction) = Unit
    override fun closeInteractionMenu() = Unit
    override fun itemSelected(itemContainerId: ItemContainerId, itemId: ItemId) = Unit
    override fun skipTurn() = Unit
    override fun onEnemyAnimationEvent(event: EnemyAnimationEvent) = Unit
}
