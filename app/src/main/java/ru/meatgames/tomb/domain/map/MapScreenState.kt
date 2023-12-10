package ru.meatgames.tomb.domain.map

import arrow.optics.optics
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.presentation.camera.animation.CameraAnimationState
import ru.meatgames.tomb.presentation.render.AnimationRenderData
import ru.meatgames.tomb.presentation.render.MapRenderTile

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
        val tilesToFadeIn: Set<ScreenSpaceCoordinates>,
        val tilesToFadeOut: Set<ScreenSpaceCoordinates>,
        val playerHealth: HealthComponent,
        val cameraAnimation: CameraAnimationState?,
    ) : MapScreenState() {
        companion object
    }
    
}
