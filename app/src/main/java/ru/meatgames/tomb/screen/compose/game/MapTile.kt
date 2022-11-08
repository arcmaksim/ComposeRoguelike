package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile

data class MapTile(
    val floorEntityTile: FloorEntityTile,
    val objectEntityTile: ObjectEntityTile? = null,
) {

    companion object {
        val initialTile = MapTile(
            floorEntityTile = FloorEntityTile.Floor,
            objectEntityTile = null,
        )
    }

}
