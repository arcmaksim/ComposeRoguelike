package ru.meatgames.tomb.domain.mapgenerator

import ru.meatgames.tomb.domain.LevelMap

interface MapGenerator {
    
    fun generateMap(
        map: LevelMap,
    ): MapConfiguration
    
}
