package ru.meatgames.tomb.render

import ru.meatgames.tomb.domain.RenderTiles

interface MapRenderTilesDecorator {

    fun processMapRenderTiles(
        mapRenderTiles: List<RenderTiles?>,
        tileLineWidth: Int,
    ): List<RenderTiles?>

}
