package ru.meatgames.tomb.new_models.room.data

@kotlinx.serialization.Serializable
class ThemedRoomsDto(
    val symbolMapping: List<ThemedRoomSymbolMappingDto>,
    val rooms: List<ThemedRoomDto>,
)
