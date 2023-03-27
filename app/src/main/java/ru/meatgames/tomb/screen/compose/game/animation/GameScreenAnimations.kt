package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.Direction

context(CoroutineScope)
suspend fun GameScreenAnimationState?.assembleGameScreenAnimations(
    animationTime: Int,
    view: View,
    shakeOffset: MutableState<IntOffset>,
    animatedOffset: MutableState<IntOffset>,
    initialAnimatedOffset: IntOffset,
    revealedTilesAlpha: MutableState<Float>,
    fadedTilesAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    val specificAnimations = when (this) {
        is GameScreenAnimationState.Shake -> {
            listOf(
                shakeOffset.asDeferredOneDirectionAnimationAsync(
                    screenShakeKeyframes,
                    Direction.Right,
                ),
                view.asDeferredRejectVibrationAsync(),
            )
        }
        is GameScreenAnimationState.Scroll -> {
            listOf(
                animatedOffset.asDeferredScrollAnimationAsync(
                    animationTime = animationTime,
                    targetValue = initialAnimatedOffset,
                ),
            )
        }
        is GameScreenAnimationState.Attack -> {
            listOf(
                shakeOffset.asDeferredOneDirectionAnimationAsync(
                    attackKeyframes,
                    direction,
                ),
                view.asDeferredConfirmVibrationAsync(250L),
            )
        }
        else -> emptyList()
    }
    
    val tilesAnimations = if (isStateless) {
        emptyList<Deferred<Any>>()
    } else {
        listOf(
            revealedTilesAlpha.asDeferredRevealAnimationAsync(animationTime),
            fadedTilesAlpha.asDeferredFadeAnimationAsync(animationTime),
        )
    }
    
    return (specificAnimations + tilesAnimations).toTypedArray()
}
