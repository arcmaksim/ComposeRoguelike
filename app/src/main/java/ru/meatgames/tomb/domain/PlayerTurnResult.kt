package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile

sealed class PlayerTurnResult {

    data class Move(
        val direction: Direction
    ) : PlayerTurnResult()

    data class Interaction(
        val coordinates: Coordinates,
        val tile: ObjectEntityTile,
    ) : PlayerTurnResult()
    
    data class ContainerInteraction(
        val coordinates: Coordinates,
        val itemContainerId: ItemContainerId,
        val itemIds: Set<ItemId>,
    ) : PlayerTurnResult()
    
    data class PickupItem(
        val coordinates: Coordinates,
        val itemContainerId: ItemContainerId,
        val itemId: ItemId,
    ) : PlayerTurnResult()

    object Block : PlayerTurnResult()

    //Attack
}
