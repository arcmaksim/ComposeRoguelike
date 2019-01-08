package ru.meatgames.tomb.new_models.room

import ru.meatgames.tomb.new_models.tile.Tile
import java.util.*

class Room(
        val name: String,
        val width: Int,
        val height: Int,
        val floor: List<String>,
        val objects: List<String>,
        val floorTiles: HashMap<String, Tile>,
        val objectTiles: HashMap<String, Tile>,
        val outerWalls: HashMap<Int, ArrayList<Pair<Int, Int>>>) {

    val connectionCells: HashMap<Int, List<Pair<Int, Int>>> = HashMap()
    val cells: ArrayList<Pair<Int, Int>> = arrayListOf()


    init {
        outerWalls.keys
                .forEach { key ->
                    outerWalls[key]
                            ?.groupBy { it.first / 7 to it.second / 7 }
                            ?.let { connectionCells[key] = it.keys.toList() }
                }

        val widthInCells = (width - 1) / 6
        val heightInCells = (height - 1) / 6
        for (i in 0 until widthInCells * heightInCells) {
            val x = i % widthInCells * 7 + 1
            val y = i / widthInCells * 7 + 1
            for (j in 0 until 25) {
                if (objects[y + j / 5][x + j % 5] != ' ') {
                    cells.add(i % widthInCells to i / widthInCells)
                    break
                }
            }
        }
    }


    fun getDirections(rnd: Random): Stack<Int> =
            Stack<Int>().apply {
                addAll(connectionCells.keys.toList().shuffled(rnd))
            }

}