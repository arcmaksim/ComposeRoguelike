package ru.meatgames.tomb.domain

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile

data class MapTile(
    val floorEntityTile: FloorEntityTile,
    val mapObject: MapObject? = null,
) {

    sealed class MapObject {
        
        data class Object(
            val objectEntityTile: ObjectEntityTile
        ) : MapObject()
        
        data class Item(
            val item: ru.meatgames.tomb.domain.item.Item,
        ) : MapObject()
        
    }
    
    companion object {
        val initialTile = MapTile(
            floorEntityTile = FloorEntityTile.Floor,
            mapObject = null,
        )
    }

}

fun ObjectEntityTile.toMapTileObject(): MapTile.MapObject.Object = MapTile.MapObject.Object(this)

data class MapTileWrapper(
    val x: Int,
    val y: Int,
    val tile: MapTile,
)
