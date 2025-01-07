package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

fun CoroutineScope.fadeInAnimationAsync(
    durationMillis: Int,
    onChange: (Float) -> Unit,
) = fadeAnimationAsync(
    durationMillis = durationMillis,
    initialValue = 0f,
    targetValue = 1f,
    onChange = onChange,
)

fun CoroutineScope.fadeOutAnimationAsync(
    durationMillis: Int,
    onChange: (Float) -> Unit,
) = fadeAnimationAsync(
    durationMillis = durationMillis,
    initialValue = 1f,
    targetValue = 0f,
    onChange = onChange,
)

private fun CoroutineScope.fadeAnimationAsync(
    durationMillis: Int,
    initialValue: Float,
    targetValue: Float,
    onChange: (Float) -> Unit,
) = async {
    animate(
        initialValue = initialValue,
        targetValue = targetValue,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(durationMillis),
        block = { animatedValue, _ ->
            onChange(animatedValue)
        },
    )
}

fun CoroutineScope.moveAnimationAsync(
    durationMillis: Int,
    targetValue: IntOffset,
    onChange: (IntOffset) -> Unit,
) = async {
    animate(
        initialValue = IntOffset.Zero,
        targetValue = targetValue,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(durationMillis),
        block = { animatedValue, _ ->
            onChange(animatedValue)
        },
    )
}
