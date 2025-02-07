package ru.meatgames.tomb.domain.item

class Item(
    val name: String,
    val id: ItemId = ItemId(),
) {
    
    override fun equals(
        other: Any?,
    ): Boolean = when (other) {
        null -> false
        !is Item -> false
        else -> other.id == id && other.name == name
    }
    
    override fun hashCode(): Int {
        var hash = 31
        hash *= id.toString().hashCode()
        hash *= name.hashCode()
        return hash
    }
    
}
