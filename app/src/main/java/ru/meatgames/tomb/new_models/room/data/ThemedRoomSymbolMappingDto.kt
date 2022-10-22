package ru.meatgames.tomb.new_models.room.data

import ru.meatgames.tomb.new_models.tile.data.ThemedTilePurposeDto

@kotlinx.serialization.Serializable
class ThemedRoomSymbolMappingDto(
    val purpose: ThemedTilePurposeDto,
    val symbol: String,
    val isConnectionTile: Boolean = false,
)
