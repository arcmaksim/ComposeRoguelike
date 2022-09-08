package ru.meatgames.tomb.new_models.themed.domain.room

import ru.meatgames.tomb.new_models.themed.data.room.ThemedRoomSymbolMappingDto
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurpose
import ru.meatgames.tomb.new_models.themed.domain.tile.toEntity

data class ThemedRoomSymbolMapping(
    val purpose: ThemedTilePurpose?,
    val symbol: Char,
    val isConnectionTile: Boolean,
)

fun ThemedRoomSymbolMappingDto.toEntity(): ThemedRoomSymbolMapping = ThemedRoomSymbolMapping(
    purpose = purpose.toEntity(),
    symbol = symbol.first(),
    isConnectionTile = isConnectionTile,
)
