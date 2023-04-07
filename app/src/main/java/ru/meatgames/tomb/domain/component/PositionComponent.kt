package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.Offset

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

fun PositionComponent.resolveDirectionTo(
    otherComponent: PositionComponent,
): Direction? {
    val xDelta = x - otherComponent.x
    val yDelta = y - otherComponent.y
    return when {
        xDelta == -1 && yDelta == 0 -> Direction.Right
        xDelta == 1 && yDelta == 0 -> Direction.Left
        yDelta == -1 && xDelta == 0 -> Direction.Bottom
        yDelta == 1 && xDelta == 0 -> Direction.Top
        else -> null
    }
}
