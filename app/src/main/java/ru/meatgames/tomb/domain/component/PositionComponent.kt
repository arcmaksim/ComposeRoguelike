package ru.meatgames.tomb.domain.component

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
