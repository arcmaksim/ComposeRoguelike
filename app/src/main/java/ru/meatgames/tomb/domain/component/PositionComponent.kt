package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.Offset
import kotlin.math.abs
import kotlin.math.absoluteValue

data class PositionComponent(
    val x: Int,
    val y: Int,
) {
    
    operator fun plus(
        increment: Offset,
    ): PositionComponent = PositionComponent(
        x = x + increment.first,
        y = y + increment.second,
    )
    
    val isValid: Boolean
        get() = x >= 0 && y >= 0
    
}

fun Coordinates.toPositionComponent(): PositionComponent = PositionComponent(
    x = first,
    y = second,
)

fun PositionComponent.toCoordinates(): Coordinates = x to y

typealias Vector = Pair<Int, Int>

fun PositionComponent.calculateVectorTo(
    otherComponent: PositionComponent,
): Vector = (x - otherComponent.x) to (y - otherComponent.y)

fun Vector.asDirections(): List<Direction> {
    fun Int.horizontalDirection(): Direction = if (this > 0) Direction.Left else Direction.Right
    fun Int.verticalDirection(): Direction = if (this > 0) Direction.Up else Direction.Down
    
    return when {
        first == 0 && second != 0 -> listOf(
            second.verticalDirection(),
            *listOf(Direction.Right, Direction.Left).shuffled().toTypedArray(),
        )
        second == 0 && first != 0 -> listOf(
            first.horizontalDirection(),
            *listOf(Direction.Up, Direction.Down).shuffled().toTypedArray(),
        )
        abs(second) == abs(first) -> listOf(second.verticalDirection(), first.horizontalDirection()).shuffled()
        else -> listOf(first.horizontalDirection(), second.verticalDirection()).shuffled()
    }
}

fun Vector.isInExactRange(
    range: Int = 1,
): Boolean = when {
    second == 0 && first.absoluteValue == range -> true
    first == 0 && second.absoluteValue == range -> true
    else -> false
}

operator fun Pair<Int, Int>.plus(
    other: Pair<Int, Int>,
): Pair<Int, Int> = first + other.first to second + other.second

operator fun Pair<Int, Int>.minus(
    other: Pair<Int, Int>,
): Pair<Int, Int> = first - other.first to second - other.second
