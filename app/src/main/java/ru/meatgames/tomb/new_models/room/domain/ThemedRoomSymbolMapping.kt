package ru.meatgames.tomb.new_models.room.domain

import ru.meatgames.tomb.new_models.room.data.ThemedRoomSymbolMappingDto
import ru.meatgames.tomb.new_models.tile.domain.ThemedTilePurpose
import ru.meatgames.tomb.new_models.tile.domain.toEntity

data class ThemedRoomSymbolMapping(
    val purpose: ThemedTilePurpose,
    val symbol: Char,
    val isConnectionTile: Boolean,
)

fun ThemedRoomSymbolMappingDto.toEntity(): ThemedRoomSymbolMapping = ThemedRoomSymbolMapping(
    purpose = purpose.toEntity(),
    symbol = symbol.first(),
    isConnectionTile = isConnectionTile,
)
