package ru.meatgames.tomb.screen.game.animation

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

context(CoroutineScope)
suspend fun MutableState<Float>.asFadeInAnimationAsync(
    durationMillis: Int,
) = asFadeAnimationAsync(
    durationMillis = durationMillis,
    initialValue = 0f,
    targetValue = 1f,
)

context(CoroutineScope)
suspend fun MutableState<Float>.asFadeOutAnimationAsync(
    durationMillis: Int,
) = asFadeAnimationAsync(
    durationMillis = durationMillis,
    initialValue = 1f,
    targetValue = 0f,
)

context(CoroutineScope)
private suspend fun MutableState<Float>.asFadeAnimationAsync(
    durationMillis: Int,
    initialValue: Float,
    targetValue: Float,
) = async {
    animate(
        initialValue = initialValue,
        targetValue = targetValue,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(durationMillis),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}

context(CoroutineScope)
suspend fun MutableState<IntOffset>.asMoveAnimationAsync(
    durationMillis: Int,
    targetValue: IntOffset,
) = async {
    animate(
        initialValue = IntOffset.Zero,
        targetValue = targetValue,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(durationMillis),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}
