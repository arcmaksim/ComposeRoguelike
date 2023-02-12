package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

context(CoroutineScope)
suspend fun getGameScreenTilesValueAnimations(
    animationTime: Int,
    revealedTilesAlpha: MutableState<Float>,
    fadedTilesAlpha: MutableState<Float>,
): List<Deferred<Unit>> = listOf(
    async { revealedTilesAlpha.getRevealAnimation(animationTime) },
    async { fadedTilesAlpha.getFadeAnimation(animationTime) },
)

private suspend fun MutableState<Float>.getRevealAnimation(
    animationTime: Int,
) = animate(
    initialValue = 1f,
    targetValue = 0f,
    typeConverter = Float.VectorConverter,
    animationSpec = tween(
        durationMillis = animationTime,
    ),
) { animatedValue, _ ->
    value = animatedValue
}

private suspend fun MutableState<Float>.getFadeAnimation(
    animationTime: Int,
) = animate(
    initialValue = 0f,
    targetValue = 1f,
    typeConverter = Float.VectorConverter,
    animationSpec = tween(
        durationMillis = animationTime,
    ),
) { animatedValue, _ ->
    value = animatedValue
}
