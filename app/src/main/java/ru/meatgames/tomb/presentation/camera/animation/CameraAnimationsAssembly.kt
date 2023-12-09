package ru.meatgames.tomb.presentation.camera.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.presentation.animation.asDirectionalKeyframeIntOffsetAnimationAsync
import ru.meatgames.tomb.presentation.animation.asMoveAnimationAsync
import ru.meatgames.tomb.presentation.multiply
import ru.meatgames.tomb.presentation.animation.asDeferredRejectVibrationAsync
import ru.meatgames.tomb.screen.game.animation.screenShakeKeyframes

context(CoroutineScope)
suspend fun CameraAnimationState?.assembleAnimations(
    animationDurationMillis: Int,
    tileSize: IntSize,
    shakeOffset: MutableState<IntOffset>,
    animatedOffset: MutableState<IntOffset>,
    view: View,
): Array<Deferred<Any>> {
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipCameraAnimations)) return emptyArray()
    
    val specificAnimation = when (this) {
        is CameraAnimationState.Smooth -> asAnimationAsync(
            animatedOffset = animatedOffset,
            targetValue = offset.multiply(tileSize),
            durationMillis = animationDurationMillis,
        )
        
        is CameraAnimationState.Shake -> asAnimationAsync(
            shakeOffset = shakeOffset,
            view = view,
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

context(CoroutineScope)
private suspend fun CameraAnimationState.Shake.asAnimationAsync(
    shakeOffset: MutableState<IntOffset>,
    view: View,
): List<Deferred<Any>> = listOf(
    shakeOffset.asDirectionalKeyframeIntOffsetAnimationAsync(
        screenShakeKeyframes,
        Direction.Right,
    ),
    view.asDeferredRejectVibrationAsync(),
)
