package ru.meatgames.tomb.render

import ru.meatgames.tomb.screen.compose.game.render.ScreenSpaceRenderTiles

interface MapRenderTilesDecorator {

    fun processMapRenderTiles(
        mapRenderTiles: List<ScreenSpaceRenderTiles?>,
        tileLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?>

}
