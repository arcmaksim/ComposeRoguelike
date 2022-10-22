package ru.meatgames.tomb.model.room.data

@kotlinx.serialization.Serializable
class ThemedRoomsDto(
    val symbolMapping: List<ThemedRoomSymbolMappingDto>,
    val rooms: List<ThemedRoomDto>,
)
