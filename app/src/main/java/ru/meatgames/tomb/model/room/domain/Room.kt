package ru.meatgames.tomb.model.room.domain

import ru.meatgames.tomb.logMessage
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

/**
 * Rotates the room clockwise
 *
 *   0 1      4 2 0
 *   2 3  ->  5 3 1
 *   4 5
 */
fun Room.rotateClockwise(): Room {
    fun Int.originalContentAtRotatedIndex(): Int =(this / height) + (height - 1 - this % height) * width
    fun Int.newIndexAfterRotation(): Int = (height - this / width - 1) + (this % width) * height
    return copy(
        width = height,
        height = width,
        floor = floor.indices.map {
            floor[it.originalContentAtRotatedIndex()]
        }.fold("") { acc, item -> acc + item },
        objects = objects.indices.map {
            objects[it.originalContentAtRotatedIndex()]
        }.fold("") { acc, item -> acc + item },
    ).apply {
        outerWalls = this@rotateClockwise.outerWalls.map {
            val originalIndex = it.first + it.second * this@rotateClockwise.width
            val translatedIndex = originalIndex.newIndexAfterRotation()
            translatedIndex % width to translatedIndex / width
        }.toSet()
    }
}

/**
 * Rotates the room counter clockwise
 *
 *   0 1      1 3 5
 *   2 3  ->  0 4 2
 *   4 5
 */
fun Room.rotateCounterclockwise(): Room {
    fun Int.originalContentAtRotatedIndex(): Int = width - 1 + (this % height) * width - this / height
    fun Int.newIndexAfterRotation(): Int = height * (width - 1 - this % width) + this / width
    return copy(
        width = height,
        height = width,
        floor = floor.indices.map {
            floor[it.originalContentAtRotatedIndex()]
        }.fold("") { acc, item -> acc + item },
        objects = objects.indices.map { index ->
            objects[index.originalContentAtRotatedIndex()]
        }.fold("") { acc, item -> acc + item },
    ).apply {
        outerWalls = this@rotateCounterclockwise.outerWalls.map {
            val originalIndex = it.first + it.second * this@rotateCounterclockwise.width
            val translatedIndex = originalIndex.newIndexAfterRotation()
            translatedIndex % width to translatedIndex / width
        }.toSet()
    }
}

/**
 * Rotates the room by 180 degrees
 *
 *   0 1      5 4
 *   2 3  ->  3 2
 *   4 5      1 0
 */
fun Room.rotate180(): Room {
    fun mapIndex(index: Int): Int = width * height - 1 - index
    return copy(
        floor = floor.reversed(),
        objects = objects.reversed(),
    ).apply {
        logMessage("Rotations", "----- 180 ------")
        outerWalls = this@rotate180.outerWalls.map {
            val originalIndex = it.first + it.second * this@rotate180.width
            val translatedIndex = mapIndex(originalIndex)
            logMessage("Rotations", "Before ${it.first} ${it.second} -> after ${translatedIndex % width} ${translatedIndex / width}")
            translatedIndex % width to translatedIndex / width
        }.toSet()
    }
}
