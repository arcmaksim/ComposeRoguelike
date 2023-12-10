package ru.meatgames.tomb.presentation.render

import ru.meatgames.tomb.domain.render.ScreenSpaceRenderTiles

interface MapRenderTilesDecorator {

    fun processMapRenderTiles(
        mapRenderTiles: List<ScreenSpaceRenderTiles?>,
        tileLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?>

}
