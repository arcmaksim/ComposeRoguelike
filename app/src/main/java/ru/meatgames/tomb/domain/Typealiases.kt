package ru.meatgames.tomb.domain

typealias Offset = Pair<Int, Int>
typealias Coordinates = Pair<Int, Int>
typealias ScreenSpaceCoordinates = Coordinates

operator fun Coordinates.plus(
    other: Coordinates,
): Coordinates = (this.first + other.first) to (this.second + other.second)

operator fun Coordinates.minus(
    other: Coordinates,
): Coordinates = (this.first - other.first) to (this.second - other.second)
