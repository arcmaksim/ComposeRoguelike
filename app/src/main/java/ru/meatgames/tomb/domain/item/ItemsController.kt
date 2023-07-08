package ru.meatgames.tomb.domain.item

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.domain.Coordinates
import javax.inject.Inject
import javax.inject.Singleton

private typealias MutableState = Triple<
    MutableMap<ItemId, Item>,
    MutableMap<ItemContainerId, ItemContainer>,
    MutableMap<Coordinates, ItemContainerId>
>

data class ItemsState(
    val items: Map<ItemId, Item> = emptyMap(),
    val containers: Map<ItemContainerId, ItemContainer> = emptyMap(),
    val containersMapping: Map<Coordinates, ItemContainerId> = emptyMap(),
)

@Singleton
class ItemsControllerImpl @Inject constructor() : ItemsController, ItemsHolder {
    
    private val _state = MutableStateFlow(ItemsState())
    val state: StateFlow<ItemsState> = _state
    
    private val mutableState: MutableState
        get() = with(state.value) {
            return Triple(
                items.toMutableMap(),
                containers.toMutableMap(),
                containersMapping.toMutableMap(),
            )
        }
    
    /** Returns [MapContainerId.Container] for now */
    override fun getMapContainerId(
        coordinates: Coordinates,
    ): MapContainerId? = with(state.value) {
        containersMapping[coordinates]?.let(MapContainerId::Container)
    }
    
    override fun getItemContainer(
        coordinates: Coordinates,
    ): ItemContainer? = with(state.value) {
        containersMapping[coordinates]?.let(containers::get)
    }
    
    override fun addItem(
        coordinates: Coordinates,
        item: Item,
    ) = with(mutableState) {
        val (items, containers, mapping) = this
        
        items[item.id] = item
        
        val itemContainerId = mapping[coordinates]
        val itemContainer = containers[itemContainerId]
        
        itemContainer?.let {
            addToContainer(
                item = item,
                container = it.copy(
                    itemIds = it.itemIds + item.id,
                ),
                containerCoordinates = coordinates,
            )
        } ?: createContainer(
            item = item,
            coordinates = coordinates,
        )
    }
    
    private fun MutableState.addToContainer(
        item: Item,
        container: ItemContainer,
        containerCoordinates: Coordinates,
    ) {
        val (_, containers, mapping) = this
        
        val newItemContainer = container.copy(
            itemIds = container.itemIds + item.id,
        )
        
        mapping[containerCoordinates] = newItemContainer.id
        containers[newItemContainer.id] = newItemContainer
    }
    
    private fun MutableState.createContainer(
        item: Item,
        coordinates: Coordinates,
    ) {
        val (_, containers, mapping) = this
        
        val itemContainer = ItemContainer(itemIds = setOf(item.id))
        
        containers[itemContainer.id] = itemContainer
        mapping[coordinates] = itemContainer.id
    }
    
    override fun takeItem(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ): Pair<Item, Boolean>? {
        val (items, containers, mapping) = mutableState
        
        val container = containers[itemContainerId] ?: return null
        val item = items[itemId] ?: return null
        if (!container.itemIds.contains(itemId)) return null
        
        val isContainerEmpty = (container.itemIds - itemId).isEmpty()
        if (isContainerEmpty) {
            containers.remove(itemContainerId)
            mapping.filterValues { it == itemContainerId }
                .forEach { mapping.remove(it.key) }
        } else {
            containers[itemContainerId] = container.copy(
                itemIds = container.itemIds - itemId,
            )
        }
        
        return item to isContainerEmpty
    }
    
    override fun clearContainers() {
        _state.value = ItemsState()
    }
    
    override suspend fun getItems(
        itemIds: Set<ItemId>,
    ): Set<Item> = itemIds
        .mapNotNull { state.value.items[it] }
        .toSet()
    
    override suspend fun getItems(
        itemContainerId: ItemContainerId,
    ): Set<Item> = with(state.value) {
        val itemContainer = containers[itemContainerId] ?: return emptySet()
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
