package ru.meatgames.tomb.domain.map

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

data class MapTileWrapper(
    val x: Int,
    val y: Int,
    val tile: MapTile,
)
