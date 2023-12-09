package ru.meatgames.tomb.presentation.animation

import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.presentation.multiply
import ru.meatgames.tomb.toIntOffset

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

/**
 * Animate IntOffset in specified direction and keyframe
 *
 * @param keyframesSpec animation keyframes
 * @param direction direction of animation
 */
context(CoroutineScope)
suspend fun MutableState<IntOffset>.asDirectionalKeyframeIntOffsetAnimationAsync(
    keyframesSpec: KeyframesSpec<Float>,
    direction: Direction,
) = async {
    val offset = direction.toIntOffset(10)
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = keyframesSpec,
    ) { animatedValue, _ ->
        value = IntOffset((offset.x * animatedValue).toInt(), (offset.y * animatedValue).toInt())
    }
}