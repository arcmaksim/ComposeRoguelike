package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.map.LevelMap

interface MapGenerator {
    
    fun generateMap(
        map: LevelMap,
    ): MapConfiguration
    
}
