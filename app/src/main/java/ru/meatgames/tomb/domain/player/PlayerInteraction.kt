package ru.meatgames.tomb.domain.player

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemContainerId

sealed class PlayerInteraction {
    
    data class SearchingContainer(
        val coordinates: Coordinates,
        val itemContainerId: ItemContainerId,
        val items: Set<Item>,
    ) : PlayerInteraction()
    
}
