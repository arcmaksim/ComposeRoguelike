package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.Coordinates

data class MapConfiguration(
    val mapWidth: Int,
    val mapHeight: Int,
    val startCoordinates: Coordinates,
)
