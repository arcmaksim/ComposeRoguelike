package ru.meatgames.tomb.screen.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.presentation.animation.asDeferredConfirmVibrationAsync
import ru.meatgames.tomb.presentation.animation.asDirectionalKeyframeIntOffsetAnimationAsync
import ru.meatgames.tomb.presentation.player.PlayerAnimation
import ru.meatgames.tomb.presentation.player.updatesScreenSpaceTiles

context(CoroutineScope)
suspend fun PlayerAnimation?.assemblePlayerInputAnimations(
    animationDurationMillis: Int,
    view: View,
    shakeOffset: MutableState<IntOffset>,
    animatedOffset: MutableState<IntOffset>,
    initialAnimatedOffset: IntOffset,
    fadeInTilesAlpha: MutableState<Float>,
    fadeOutTilesAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipPlayerAnimations)) return emptyArray()
    
    val specificAnimations = when (this) {
        is PlayerAnimation.Move -> asAnimationAsync(
            animatedOffset = animatedOffset,
            targetValue = initialAnimatedOffset,
            durationMillis = animationDurationMillis,
        )
        
        is PlayerAnimation.Attack -> asAnimationAsync(
            shakeOffset = shakeOffset,
            view = view,
        )
        
        else -> emptyList()
    }
    
    val tilesAnimations = if (updatesScreenSpaceTiles) {
        listOf(
            fadeInTilesAlpha.asFadeInAnimationAsync(animationDurationMillis),
            fadeOutTilesAlpha.asFadeOutAnimationAsync(animationDurationMillis),
        )
    } else {
        emptyList<Deferred<Any>>()
    }
    
    return (specificAnimations + tilesAnimations).toTypedArray()
}

context(CoroutineScope)
private suspend fun PlayerAnimation.Move.asAnimationAsync(
    animatedOffset: MutableState<IntOffset>,
    targetValue: IntOffset,
    durationMillis: Int,
): List<Deferred<Any>> = listOf(
    animatedOffset.asMoveAnimationAsync(
        durationMillis = durationMillis,
        targetValue = targetValue,
    ),
)

context(CoroutineScope)
private suspend fun PlayerAnimation.Attack.asAnimationAsync(
    shakeOffset: MutableState<IntOffset>,
    view: View,
): List<Deferred<Any>> = listOf(
    shakeOffset.asDirectionalKeyframeIntOffsetAnimationAsync(
        defaultAttackKeyframes,
        direction,
    ),
    view.asDeferredConfirmVibrationAsync(DEFAULT_ATTACK_DELAY_MILLIS),
)