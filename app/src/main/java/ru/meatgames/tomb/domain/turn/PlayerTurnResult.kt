package ru.meatgames.tomb.domain.turn

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
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
        val itemContainerId: ItemContainerId,
        val itemId: ItemId,
    ) : PlayerTurnResult()

    object Block : PlayerTurnResult()

    data class Attack(
        val direction: Direction,
    ) : PlayerTurnResult()
    
}

fun PlayerTurnResult.finishesPlayerTurn(): Boolean = when (this) {
    is PlayerTurnResult.Block,
    is PlayerTurnResult.ContainerInteraction -> false
    else -> true
}

fun PlayerTurnResult.hasAnimation(): Boolean = when (this) {
    is PlayerTurnResult.PickupItem -> false
    else -> true
}