package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemBag
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile

sealed class PlayerMoveResult {

    data class Move(
        val direction: Direction
    ) : PlayerMoveResult()

    data class Interaction(
        val coordinates: Coordinates,
        val tile: ObjectEntityTile,
    ) : PlayerMoveResult()
    
    data class ItemBagInteraction(
        val itemBag: ItemBag,
    ) : PlayerMoveResult()

    object Block : PlayerMoveResult()

    //Attack
}
