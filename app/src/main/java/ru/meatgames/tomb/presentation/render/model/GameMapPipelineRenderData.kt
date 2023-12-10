package ru.meatgames.tomb.presentation.render.model

import ru.meatgames.tomb.domain.ScreenSpaceCoordinates

data class GameMapPipelineRenderData(
    val tiles: List<MapRenderTile>,
    val tilesToFadeIn: List<ScreenSpaceCoordinates>,
    val tilesToFadeOut: List<ScreenSpaceCoordinates>,
)
