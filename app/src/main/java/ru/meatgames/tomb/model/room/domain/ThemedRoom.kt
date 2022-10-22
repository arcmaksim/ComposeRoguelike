package ru.meatgames.tomb.model.room.domain

import ru.meatgames.tomb.model.room.data.ThemedRoomDto

data class ThemedRoom(
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
        symbolMapping: List<ThemedRoomSymbolMapping>,
    ) : this(
        name = name,
        width = width,
        height = height,
        floor = floor,
        objects = objects,
    ) {
        val outerSymbols = symbolMapping.asSequence()
            .filter { it.isConnectionTile }
            .map { it.symbol }
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

fun ThemedRoomDto.toEntity(
    symbolMapping: List<ThemedRoomSymbolMapping>,
): ThemedRoom = ThemedRoom(
    name = name,
    width = width,
    height = height,
    floor = floor.fold("") { acc, item -> acc + item },
    objects = objects.fold("") { acc, item -> acc + item },
    symbolMapping = symbolMapping,
)
