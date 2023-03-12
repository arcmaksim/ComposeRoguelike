package ru.meatgames.tomb.domain.item

sealed class MapContainerId {
    
    data class Item(val itemId: ItemId) : MapContainerId()
    
    data class Container(val itemContainerId: ItemContainerId) : MapContainerId()
    
}
