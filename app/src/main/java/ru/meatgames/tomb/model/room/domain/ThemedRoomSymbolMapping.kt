package ru.meatgames.tomb.model.room.domain

import ru.meatgames.tomb.model.room.data.ThemedRoomSymbolMappingDto
import ru.meatgames.tomb.model.tile.domain.ThemedTilePurpose
import ru.meatgames.tomb.model.tile.domain.toEntity

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
