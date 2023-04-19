package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.render.MapRenderTile

data class GameMapPipelineRenderData(
    val tiles: List<MapRenderTile>,
    val tilesToFadeIn: List<ScreenSpaceCoordinates>,
    val tilesToFadeOut: List<ScreenSpaceCoordinates>,
)
