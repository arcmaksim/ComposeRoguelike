package ru.meatgames.tomb.presentation.render

interface MapRenderTilesDecorator {

    fun processMapRenderTiles(
        mapRenderTiles: List<ScreenSpaceRenderTiles?>,
        tileLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?>

}
