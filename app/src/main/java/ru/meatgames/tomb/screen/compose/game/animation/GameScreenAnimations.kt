package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.domain.PlayerAnimationState
import ru.meatgames.tomb.domain.isStateless

context(CoroutineScope)
suspend fun PlayerAnimationState?.assembleGameScreenAnimations(
    animationTime: Int,
    view: View,
    shakeHorizontalOffset: MutableState<Float>,
    animatedOffset: MutableState<IntOffset>,
    initialAnimatedOffset: IntOffset,
    revealedTilesAlpha: MutableState<Float>,
    fadedTilesAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    val specificAnimations = when (this) {
        is PlayerAnimationState.Shake -> {
            listOf(
                shakeHorizontalOffset.asDeferredScreenShakeAnimationAsync(),
                view.asDeferredVibrationAsync(),
            )
        }
        is PlayerAnimationState.Scroll -> {
            listOf(
                animatedOffset.asDeferredScrollAnimationAsync(
                    animationTime = animationTime,
                    targetValue = initialAnimatedOffset,
                ),
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
