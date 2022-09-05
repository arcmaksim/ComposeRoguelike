package ru.meatgames.tomb.new_models.room

import ru.meatgames.tomb.new_models.tile.Tile

data class Room(
    val width: Int,
    val height: Int,
    val floor: List<Char>,
    val objects: List<Char>,
    val floorTiles: Map<Char, Tile>,
    val objectTiles: Map<Char, Tile>,
    val outerWalls: ArrayList<Pair<Int, Int>>,
)