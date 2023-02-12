package ru.meatgames.tomb.screen.compose.game.render

import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.render.MapRenderTile

data class GameMapPipelineRenderData(
    val tiles: List<MapRenderTile>,
    val tilesToReveal: List<ScreenSpaceCoordinates>,
    val tilesToFade: List<ScreenSpaceCoordinates>,
)
