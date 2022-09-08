package ru.meatgames.tomb.new_models.themed.domain.room

import ru.meatgames.tomb.new_models.themed.data.room.ThemedRoomDto

class ThemedRoom(
    val name: String,
    val width: Int,
    val height: Int,
    val floor: String,
    val objects: String,
)

fun ThemedRoomDto.toEntity(): ThemedRoom = ThemedRoom(
    name = name,
    width = width,
    height = height,
    floor = floor.fold("") { acc, item -> acc + item },
    objects = objects.fold("") { acc, item -> acc + item },
)
