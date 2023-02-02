package ru.meatgames.tomb.model.tile.domain

import kotlinx.serialization.Serializable

@Serializable
enum class FloorEntityTile {
    Floor
}

@Serializable
enum class ObjectEntityTile {
    Wall,
    DoorClosed,
    DoorOpened,
    StairsUp,
    StairsDown,
    Gismo,
}
