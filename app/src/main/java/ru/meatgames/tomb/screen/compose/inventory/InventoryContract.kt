package ru.meatgames.tomb.screen.compose.inventory

import ru.meatgames.tomb.domain.item.Item

enum class InventoryEvent {
    Back,
}

data class InventoryState(
    val items: List<Item> = emptyList(),
)
