package ru.meatgames.tomb.render

sealed class MapRenderTile {

    object Empty : MapRenderTile()

    data class Content(
        val floorData: RenderData,
        val objectData: RenderData?,
        val itemData: RenderData?,
        val enemyData: AnimationRenderData?,
        val isVisible: Boolean,
    ) : MapRenderTile()

}
