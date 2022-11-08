package ru.meatgames.tomb.render

sealed class MapRenderTile {

    object Hidden : MapRenderTile()

    data class Revealed(
        val floorData: RenderData,
        val objectData: RenderData?,
    ) : MapRenderTile()

}
