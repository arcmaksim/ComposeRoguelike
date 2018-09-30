package ru.meatgames.tomb.new_models.map

import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile
import kotlin.properties.Delegates

class MapTile {

    var isPassable: Boolean = false
        private set
    var isUsable: Boolean = false
        private set
    var isTransparent: Boolean = false
        private set

    lateinit var floorTile: Tile
    var objectTile: Tile by Delegates.observable(TileRepo.emptyTile) { _, _, newTile ->
        isPassable = newTile.isPassable
        isUsable = newTile.isUsable
        isTransparent = newTile.isTransparent
    }

}