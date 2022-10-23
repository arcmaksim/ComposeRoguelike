package ru.meatgames.tomb.model.room.domain

import ru.meatgames.tomb.model.room.data.RoomSymbolMappingDto
import ru.meatgames.tomb.model.tile.domain.TilePurpose
import ru.meatgames.tomb.model.tile.domain.toEntity

data class RoomSymbolMapping(
    val purpose: TilePurpose,
    val symbol: Char,
    val isConnectionTile: Boolean,
)

fun RoomSymbolMappingDto.toEntity(): RoomSymbolMapping = RoomSymbolMapping(
    purpose = purpose.toEntity(),
    symbol = symbol.first(),
    isConnectionTile = isConnectionTile,
)
