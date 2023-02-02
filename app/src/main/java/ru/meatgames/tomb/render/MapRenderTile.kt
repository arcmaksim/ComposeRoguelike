package ru.meatgames.tomb.render

sealed class MapRenderTile {

    data class Hidden(
        val effectData: RenderData? = null,
    ) : MapRenderTile()

    data class Revealed(
        val floorData: RenderData,
        val objectData: RenderData?,
    ) : MapRenderTile()

}
