package ru.meatgames.tomb.new_models.room

import ru.meatgames.tomb.new_models.tile.Tile

class Room(
		val width: Int,
		val height: Int,
		val floor: List<String>,
		val objects: List<String>,
		val floorTiles: HashMap<String, Tile>,
		val objectTiles: HashMap<String, Tile>,
		val outerWalls: HashMap<Int, ArrayList<Pair<Int, Int>>>)