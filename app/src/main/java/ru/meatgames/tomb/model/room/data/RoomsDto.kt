package ru.meatgames.tomb.model.room.data

import kotlinx.serialization.Serializable
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping

@Serializable
class RoomsDto(
    val floorMapping: List<FloorTileMapping>,
    val objectMapping: List<ObjectTileMapping>,
    val rooms: List<RoomDto>,
)

@Serializable
class RoomDto(
    val name: String,
    val width: Int,
    val height: Int,
    val floor: List<String>,
    val objects: List<String>,
)
