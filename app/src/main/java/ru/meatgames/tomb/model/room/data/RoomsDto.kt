package ru.meatgames.tomb.model.room.data

@kotlinx.serialization.Serializable
class RoomsDto(
    val symbolMapping: List<RoomSymbolMappingDto>,
    val rooms: List<RoomDto>,
)
