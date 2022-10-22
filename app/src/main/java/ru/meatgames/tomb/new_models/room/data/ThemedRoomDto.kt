package ru.meatgames.tomb.new_models.room.data

@kotlinx.serialization.Serializable
class ThemedRoomDto(
    val name: String,
    val width: Int,
    val height: Int,
    val floor: List<String>,
    val objects: List<String>,
)
