package ru.meatgames.tomb.domain.item

data class ItemContainer(
    val itemIds: Set<ItemId> = emptySet(),
    val id: ItemContainerId = ItemContainerId(),
)
