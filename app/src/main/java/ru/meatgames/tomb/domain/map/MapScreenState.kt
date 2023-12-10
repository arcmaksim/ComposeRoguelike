package ru.meatgames.tomb.domain.map

import arrow.optics.optics
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.presentation.camera.animation.CameraAnimationState
import ru.meatgames.tomb.presentation.render.model.AnimationRenderData
import ru.meatgames.tomb.presentation.render.model.MapRenderTile
import ru.meatgames.tomb.presentation.tiles.animation.TilesAnimationState

@optics
sealed class MapScreenState {
    
    companion object {}
    
    data object Loading : MapScreenState()
    
    @optics
    data class Ready(
        val characterRenderData: AnimationRenderData,
        val tiles: List<MapRenderTile?>,
        val tilesWidth: Int,
        val tilesPadding: Int,
        val viewportWidth: Int,
        val viewportHeight: Int,
        val playerHealth: HealthComponent,
        val cameraAnimation: CameraAnimationState?,
        val tilesAnimation: TilesAnimationState?,
    ) : MapScreenState() {
        companion object
    }
    
}
