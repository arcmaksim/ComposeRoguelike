package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.domain.item.ItemBag

sealed class GameScreenInteractionState {
    
    data class SearchingContainer(
        val itemBag: ItemBag,
    ) : GameScreenInteractionState()
    
}