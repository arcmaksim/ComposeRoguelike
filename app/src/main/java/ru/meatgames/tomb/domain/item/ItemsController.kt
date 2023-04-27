package ru.meatgames.tomb.domain.item

import ru.meatgames.tomb.domain.Coordinates
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsControllerImpl @Inject constructor() : ItemsController, ItemsHolder {
    
    private val items = mutableMapOf<ItemId, Item>()
    private val itemContainers = mutableMapOf<ItemContainerId, ItemContainer>()
    private val itemContainersMapping = mutableMapOf<Coordinates, ItemContainerId>()
    
    /** Returns [MapContainerId.Container] for now */
    override fun getMapContainerId(
        coordinates: Coordinates,
    ): MapContainerId? = itemContainersMapping[coordinates]?.let(MapContainerId::Container)
    
    override fun getItemContainer(
        coordinates: Coordinates,
    ): ItemContainer? = itemContainersMapping[coordinates]?.let(itemContainers::get)
    
    override fun addItem(
        coordinates: Coordinates,
        item: Item,
    ) {
        items[item.id] = item
    
        val itemContainerId = itemContainersMapping[coordinates]
        val itemContainer = itemContainers[itemContainerId]
        
        itemContainer?.let {
            item.addToContainer(
                itemContainer = it,
                itemContainerCoordinates = coordinates,
            )
        } ?: coordinates.createContainer(item)
    }
    
    private fun Item.addToContainer(
        itemContainer: ItemContainer,
        itemContainerCoordinates: Coordinates,
    ) {
        val newItemContainer = itemContainer.copy(
            itemIds = itemContainer.itemIds + id,
        )
        
        itemContainersMapping[itemContainerCoordinates] = newItemContainer.id
        itemContainers[newItemContainer.id] = newItemContainer
    }
    
    private fun Coordinates.createContainer(
        item: Item,
    ) {
        val itemContainer = ItemContainer(itemIds = setOf(item.id))
        
        itemContainers[itemContainer.id] = itemContainer
        itemContainersMapping[this] = itemContainer.id
    }
    
    override fun takeItem(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ): Pair<Item, Boolean>? {
        val container = itemContainers[itemContainerId] ?: return null
        val item = items[itemId] ?: return null
        if (!container.itemIds.contains(itemId)) return null
        
        val isContainerEmpty = (container.itemIds - itemId).isEmpty()
        if (isContainerEmpty) {
            itemContainers.remove(itemContainerId)
            itemContainersMapping.filterValues { it == itemContainerId }
                .forEach { itemContainersMapping.remove(it.key) }
        } else {
            itemContainers[itemContainerId] = container.copy(
                itemIds = container.itemIds - itemId,
            )
        }
        
        return item to isContainerEmpty
    }
    
    override fun clearContainers() {
        itemContainersMapping.clear()
        
        itemContainers.values.forEach { container ->
            container.itemIds.forEach(items::remove)
        }
        itemContainers.clear()
    }
    
    override suspend fun getItems(
        itemIds: Set<ItemId>,
    ): Set<Item> = itemIds.mapNotNull { items[it] }.toSet()
    
    override suspend fun getItems(
        itemContainerId: ItemContainerId,
    ): Set<Item> {
        val itemContainer = itemContainers[itemContainerId] ?: return emptySet()
        return itemContainer.itemIds.mapNotNull { items[it] }.toSet()
    }
    
}

// write interface
interface ItemsController {
    
    fun addItem(
        coordinates: Coordinates,
        item: Item,
    )
    
    fun takeItem(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ): Pair<Item, Boolean>?
    
}

// readonly interface
interface ItemsHolder {
    
    fun getMapContainerId(
        coordinates: Coordinates,
    ): MapContainerId?
    
    fun getItemContainer(
        coordinates: Coordinates,
    ): ItemContainer?
    
    fun clearContainers()
    
    suspend fun getItems(
        itemContainerId: ItemContainerId,
    ): Set<Item>
    
    suspend fun getItems(
        itemIds: Set<ItemId>,
    ): Set<Item>
    
}
