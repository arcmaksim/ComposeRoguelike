package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

context(CoroutineScope)
suspend fun MutableState<Float>.asDeferredRevealAnimationAsync(
    animationTime: Int,
) = async {
    animate(
        initialValue = 1f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(
            durationMillis = animationTime,
        ),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}

context(CoroutineScope)
suspend fun MutableState<Float>.asDeferredFadeAnimationAsync(
    animationTime: Int,
) = async {
    animate(
        initialValue = 0f,
        targetValue = 1f,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(
            durationMillis = animationTime,
        ),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}

context(CoroutineScope)
suspend fun MutableState<IntOffset>.asDeferredScrollAnimationAsync(
    animationTime: Int,
    targetValue: IntOffset,
) = async {
    animate(
        initialValue = IntOffset.Zero,
        targetValue = targetValue,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(
            durationMillis = animationTime,
        ),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}
