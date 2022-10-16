package ru.meatgames.tomb.new_models.themed.data.room

@kotlinx.serialization.Serializable
class ThemedRoomsDto(
    val symbolMapping: List<ThemedRoomSymbolMappingDto>,
    val rooms: List<ThemedRoomDto>,
)
