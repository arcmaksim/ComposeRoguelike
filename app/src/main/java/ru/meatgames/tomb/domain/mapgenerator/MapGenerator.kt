package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.LevelMap
import ru.meatgames.tomb.domain.MapConfiguration

interface MapGenerator {
    
    fun generateMap(
        map: LevelMap,
    ): MapConfiguration
    
}
