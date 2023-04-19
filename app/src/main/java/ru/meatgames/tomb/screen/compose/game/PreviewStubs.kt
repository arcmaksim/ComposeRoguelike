package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId

internal val navigatorPreviewStub = object : GameScreenNavigator {
    override fun onNewMapRequest() = Unit
    
    override fun navigateToInventory() = Unit
    
    override fun navigateToCharacterSheet() = Unit
}

internal val interactionControllerPreviewStub = object : GameScreenInteractionController {
    override suspend fun finishPlayerAnimation() = Unit
    
    override suspend fun finishEnemiesAnimation() = Unit
    
    override fun processCharacterMoveInput(
        direction: Direction,
    ) = Unit
    
    override fun closeInteractionMenu() = Unit
    
    override fun itemSelected(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) = Unit
}
