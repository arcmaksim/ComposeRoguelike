package ru.meatgames.tomb.model.tile.data

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile

@kotlinx.serialization.Serializable
class FloorTileMapping(
    val entity: FloorEntityTile,
    val symbol: String,
)
