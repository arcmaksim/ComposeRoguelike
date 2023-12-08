package ru.meatgames.tomb.presentation.camera.animation

import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.presentation.multiply

context(CoroutineScope)
suspend fun CameraAnimationState?.assembleAnimations(
    animationDurationMillis: Int,
    tileSize: IntSize,
    animatedOffset: MutableState<IntOffset>,
): Array<Deferred<Any>> {
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipCameraAnimations)) return emptyArray()
    
    val specificAnimation = when (this) {
        is CameraAnimationState.Smooth -> asAnimationAsync(
            animatedOffset = animatedOffset,
            targetValue = offset.multiply(tileSize),
            durationMillis = animationDurationMillis,
        )
        
        else -> emptyList()
    }
    
    return specificAnimation.toTypedArray()
}

context(CoroutineScope)
private suspend fun CameraAnimationState.Smooth.asAnimationAsync(
    animatedOffset: MutableState<IntOffset>,
    targetValue: IntOffset,
    durationMillis: Int,
): List<Deferred<Any>> = listOf(
    animatedOffset.asMoveAnimationAsync(
        durationMillis = durationMillis,
        initialValue = IntOffset.Zero,
        targetValue = targetValue,
    ),
)