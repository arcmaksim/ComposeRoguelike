package ru.meatgames.tomb.domain.map

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.logMessage

class LevelMap(
    val width: Int,
    val height: Int,
) {
    
    val array = Array(width * height) { MapTile.initialTile }
    val indices = array.indices
    
    fun getTile(
        x: Int,
        y: Int,
    ): MapTile? {
        val index = calcIndex(x, y)
        return getTile(index)
    }

    fun getTile(
        coordinates: Coordinates,
    ): MapTile? {
        val index = calcIndex(coordinates.first, coordinates.second)
        return getTile(index)
    }

    fun getTile(
        index: Int,
    ): MapTile? {
        if (index !in indices) {
            logMessage(
                tag = "LevelMap",
                message = "getTile - index out of bounds - $indices",
            )
            return null
        }
        return array[index]
    }
    
    fun updateSingleTile(
        x: Int,
        y: Int,
        update: MapTile.() -> MapTile,
    ) {
        val index = calcIndex(x, y)
        if (!updateTile(index, update)) return
    }
    
    private fun updateTile(
        index: Int,
        update: MapTile.() -> MapTile,
    ): Boolean {
        if (index !in indices) {
            logMessage(
                tag = "LevelMap",
                message = "getTile - index out of bounds - $indices",
            )
            return false
        }
        
        array[index] = array[index].update()
        Flags.mapDirty.value = true
        return true
    }
    
    private fun calcIndex(
        x: Int,
        y: Int,
    ) = x + y * width
    
}
