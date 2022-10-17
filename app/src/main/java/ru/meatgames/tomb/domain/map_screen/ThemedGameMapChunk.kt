package ru.meatgames.tomb.domain.map_screen

import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
import ru.meatgames.tomb.screen.compose.game.themedViewportHeight
import ru.meatgames.tomb.screen.compose.game.themedViewportWidth

data class ThemedGameMapChunk(
    val width: Int = themedViewportWidth,
    val height: Int = themedViewportHeight,
    val mapOffsetX: Int,
    val mapOffsetY: Int,
    val gameMapTiles: List<ThemedGameMapTile>,
)
