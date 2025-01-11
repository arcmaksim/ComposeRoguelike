package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.domain.player.PlayerAnimation
import ru.meatgames.tomb.domain.player.updatesScreenSpaceTiles

fun PlayerAnimation?.assemblePlayerInputAnimations(
    scope: CoroutineScope,
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
        is PlayerAnimation.Shake -> asAnimationAsync(
            scope = scope,
            shakeOffset = shakeOffset,
            view = view,
        )

        is PlayerAnimation.Move -> asAnimationAsync(
            scope = scope,
            animatedOffset = animatedOffset,
            targetValue = initialAnimatedOffset,
            durationMillis = animationDurationMillis,
        )

        is PlayerAnimation.Attack -> asAnimationAsync(
            scope = scope,
            shakeOffset = shakeOffset,
            view = view,
        )

        else -> emptyList()
    }

    val tilesAnimations = if (updatesScreenSpaceTiles) {
        listOf(
            scope.fadeInAnimationAsync(
                durationMillis = animationDurationMillis,
                onChange = {
                    fadeInTilesAlpha.value = it
                },
            ),
            scope.fadeOutAnimationAsync(
                durationMillis = animationDurationMillis,
                onChange = {
                    fadeOutTilesAlpha.value = it
                },
            ),
        )
    } else {
        emptyList<Deferred<Any>>()
    }

    return (specificAnimations + tilesAnimations).toTypedArray()
}

private fun PlayerAnimation.Shake.asAnimationAsync(
    scope: CoroutineScope,
    shakeOffset: MutableState<IntOffset>,
    view: View,
): List<Deferred<Any>> = listOf(
    scope.directionalKeyframeIntOffsetAnimationAsync(
        screenShakeKeyframes,
        Direction.Right,
        onChange = {
            shakeOffset.value = it
        },
    ),
    scope.rejectVibrationAsync(
        view = view,
    ),
)

private fun PlayerAnimation.Move.asAnimationAsync(
    scope: CoroutineScope,
    animatedOffset: MutableState<IntOffset>,
    targetValue: IntOffset,
    durationMillis: Int,
): List<Deferred<Any>> = listOf(
    scope.moveAnimationAsync(
        durationMillis = durationMillis,
        targetValue = targetValue,
        onChange = {
            animatedOffset.value = it
        },
    ),
)

private fun PlayerAnimation.Attack.asAnimationAsync(
    scope: CoroutineScope,
    shakeOffset: MutableState<IntOffset>,
    view: View,
): List<Deferred<Any>> = listOf(
    scope.directionalKeyframeIntOffsetAnimationAsync(
        defaultAttackKeyframes,
        direction,
        onChange = {
            shakeOffset.value = it
        },
    ),
    scope.confirmVibrationAsync(
        view = view,
        delay = 250L,
    ),
)