package ru.meatgames.tomb.render

import ru.meatgames.tomb.domain.render.ScreenSpaceRenderTiles

interface MapRenderTilesDecorator {

    fun processMapRenderTiles(
        mapRenderTiles: List<ScreenSpaceRenderTiles?>,
        tileLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?>

}
