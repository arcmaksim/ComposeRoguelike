package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.Offset
import kotlin.math.abs

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
    fun Int.verticalDirection(): Direction = if (this > 0) Direction.Top else Direction.Bottom
    
    return when {
        first == 0 && second != 0 -> listOf(second.verticalDirection())
        second == 0 && first != 0 -> listOf(first.horizontalDirection())
        abs(second) == abs(first) -> listOf(second.verticalDirection(), first.horizontalDirection())
        else -> listOf(first.horizontalDirection(), second.verticalDirection())
    }
}

fun Vector.isNextTo(): Boolean = when {
    (first == 1 || first == -1) && second == 0 -> true
    (second == 1 || second == -1) && first == 0 -> true
    else -> false
}
