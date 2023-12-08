package ru.meatgames.tomb.presentation.camera.animation

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

context(CoroutineScope)
suspend fun MutableState<IntOffset>.asMoveAnimationAsync(
    durationMillis: Int,
    initialValue: IntOffset,
    targetValue: IntOffset,
) = async {
    animate(
        initialValue = initialValue,
        targetValue = targetValue,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(durationMillis),
    ) { animatedValue, _ ->
        value = animatedValue
    }
}
