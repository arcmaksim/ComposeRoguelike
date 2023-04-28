package ru.meatgames.tomb.domain.item

data class ItemContainer(
    val id: ItemContainerId = ItemContainerId(),
    val itemIds: Set<ItemId> = emptySet(),
)
