package ru.meatgames.tomb.model.room.data

import ru.meatgames.tomb.model.tile.data.TilePurposeDto

@kotlinx.serialization.Serializable
class RoomSymbolMappingDto(
    val purpose: TilePurposeDto,
    val symbol: String,
    val isConnectionTile: Boolean = false,
)
