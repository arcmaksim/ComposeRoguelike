package ru.meatgames.tomb.screen.compose.inventory

import ru.meatgames.tomb.domain.item.Item

data class InventoryState(
    val items: List<Item> = emptyList(),
)
