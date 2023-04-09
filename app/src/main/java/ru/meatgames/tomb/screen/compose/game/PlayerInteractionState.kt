package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemContainerId

sealed class PlayerInteractionState {
    
    data class SearchingContainer(
        val coordinates: Coordinates,
        val itemContainerId: ItemContainerId,
        val items: Set<Item>,
    ) : PlayerInteractionState()
    
}