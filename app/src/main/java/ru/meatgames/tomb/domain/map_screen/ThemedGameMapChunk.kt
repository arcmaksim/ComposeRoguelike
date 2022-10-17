package ru.meatgames.tomb.domain.map_screen

import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
import ru.meatgames.tomb.screen.compose.game.viewportHeight
import ru.meatgames.tomb.screen.compose.game.viewportWidth

data class ThemedGameMapChunk(
    val width: Int = viewportWidth,
    val height: Int = viewportHeight,
    val mapOffsetX: Int,
    val mapOffsetY: Int,
    val gameMapTiles: List<ThemedGameMapTile>,
)
