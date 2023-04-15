package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes

internal val screenShakeKeyframes: AnimationSpec<Float> = keyframes {
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

fun produceAttackKeyFrames(
    durationInMillis: Int,
): AnimationSpec<Float> = keyframes {
    durationMillis = durationInMillis
    
    -.5f at (durationInMillis / 2) with FastOutSlowInEasing
    1f at durationInMillis - (durationInMillis / 4) with FastOutSlowInEasing
    0f at durationInMillis with FastOutSlowInEasing
}

internal val defaultAttackKeyframes = produceAttackKeyFrames(ENEMIES_ATTACK_DURATION_MILLIS)
