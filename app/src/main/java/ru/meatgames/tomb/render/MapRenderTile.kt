package ru.meatgames.tomb.render

sealed class MapRenderTile {

    data object Empty : MapRenderTile()

    data class Content(
        val floorData: RenderData,
        val objectData: RenderData?,
        val itemData: RenderData?,
        val enemyData: AnimationRenderData?,
        // TODO: add render order
        val decorations: List<RenderData> = emptyList(),
        val isVisible: Boolean,
    ) : MapRenderTile()

}
