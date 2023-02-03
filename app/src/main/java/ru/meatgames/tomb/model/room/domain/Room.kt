package ru.meatgames.tomb.model.room.domain

import ru.meatgames.tomb.model.room.data.RoomDto
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping

data class Room(
    val name: String,
    val width: Int,
    val height: Int,
    val floor: String,
    val objects: String,
) {

    var outerWalls: Set<Pair<Int, Int>> = emptySet()
        private set

    constructor(
        name: String,
        width: Int,
        height: Int,
        floor: String,
        objects: String,
        objectMapping: List<ObjectTileMapping>,
    ) : this(
        name = name,
        width = width,
        height = height,
        floor = floor,
        objects = objects,
    ) {
        val outerSymbols = objectMapping.asSequence()
            .filter { it.isConnectionTile }
            .map { it.symbol.first() }
            .toSet()

        outerWalls = objects.mapIndexedNotNull { index, char ->
            if (outerSymbols.contains(char)) {
                index % width to index / width
            } else {
                null
            }
        }.toSet()
    }

}

fun RoomDto.toEntity(
    objectMapping: List<ObjectTileMapping>,
): Room = Room(
    name = name,
    width = width,
    height = height,
    floor = floor.fold("") { acc, item -> acc + item },
    objects = objects.fold("") { acc, item -> acc + item },
    objectMapping = objectMapping,
)
