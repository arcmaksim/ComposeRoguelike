package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.keyframes

internal val screenShakeKeyframes: KeyframesSpec<Float> = keyframes {
    durationMillis = ENEMIES_ATTACK_DURATION_MILLIS
    
    val easing = FastOutLinearInEasing
    val animationSteps = 5
    val animationStep = ENEMIES_ATTACK_DURATION_MILLIS / animationSteps
    
    (0 until animationSteps).forEach {
        val value = if (it % 2 == 0) 1f else -1f
        -value at animationStep * it with easing
    }
    
    0f at 300 with easing
}

fun produceAttackKeyframes(
    durationMillis: Int,
): KeyframesSpec<Float> = keyframes {
    this.durationMillis = durationMillis
    
    -.5f at (durationMillis / 2) with FastOutSlowInEasing
    1f at durationMillis - (durationMillis / 4) with FastOutSlowInEasing
    0f at durationMillis with FastOutSlowInEasing
}

internal val defaultAttackKeyframes = produceAttackKeyframes(ENEMIES_ATTACK_DURATION_MILLIS)

internal fun produceIconKeyframes(
    durationMillis: Int,
): KeyframesSpec<Float> = keyframes {
    this.durationMillis = durationMillis
    
    val easing = FastOutLinearInEasing
    val transitionDurationMillis = 50
    
    0f at 0 with easing
    1f at transitionDurationMillis with easing
    1f at durationMillis - transitionDurationMillis with easing
    0f at durationMillis with easing
}
