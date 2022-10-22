package ru.meatgames.tomb.model.room.data

import ru.meatgames.tomb.model.tile.data.ThemedTilePurposeDto

@kotlinx.serialization.Serializable
class ThemedRoomSymbolMappingDto(
    val purpose: ThemedTilePurposeDto,
    val symbol: String,
    val isConnectionTile: Boolean = false,
)
