package ru.meatgames.tomb.presentation.render.model

import arrow.optics.optics

@optics
sealed class MapRenderTile {
    
    companion object {}

    data object Empty : MapRenderTile()

    @optics
    data class Content(
        val floorData: RenderData,
        val objectData: RenderData?,
        val itemData: RenderData?,
        val enemyData: AnimationRenderData?,
        // TODO: add render order
        val decorations: List<RenderData> = emptyList(),
        val isVisible: Boolean,
    ) : MapRenderTile() {
        companion object
    }

}
