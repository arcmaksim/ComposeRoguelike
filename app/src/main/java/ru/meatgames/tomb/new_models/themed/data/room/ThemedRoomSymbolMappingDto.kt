package ru.meatgames.tomb.new_models.themed.data.room

import ru.meatgames.tomb.new_models.themed.data.ThemedTilePurposeDto

@kotlinx.serialization.Serializable
class ThemedRoomSymbolMappingDto(
    val purpose: ThemedTilePurposeDto?,
    val symbol: String,
    val isConnectionTile: Boolean = false,
)
