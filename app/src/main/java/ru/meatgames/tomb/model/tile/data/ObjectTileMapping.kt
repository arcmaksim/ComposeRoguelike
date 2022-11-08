package ru.meatgames.tomb.model.tile.data

import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile

@kotlinx.serialization.Serializable
class ObjectTileMapping(
    val entity: ObjectEntityTile?,
    val symbol: String,
    val isConnectionTile: Boolean = false,
)
