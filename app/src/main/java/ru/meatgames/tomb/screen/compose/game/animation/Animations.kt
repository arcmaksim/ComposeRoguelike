package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.toIntOffset

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

/**
 * Animate enemy movement
 *
 * @param durationMillis animation time in milliseconds
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
context(CoroutineScope)
suspend fun asEnemiesMoveAnimationAsync(
    durationMillis: Int,
    delayMillis: Int = 0,
    update: (Float) -> Unit,
) = async {
    animate(
        initialValue = 1f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis),
    ) { animatedValue, _ ->
        update(animatedValue)
    }
}

/**
 * Animate enemy attack
 *
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
context(CoroutineScope)
suspend fun asEnemiesAttackAnimationAsync(
    delayMillis: Int,
    update: (Float) -> Unit,
) = async {
    delay(delayMillis.toLong())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = defaultAttackKeyframes,
    ) { animatedValue, _ ->
        update(animatedValue)
    }
}

/**
 * Animate skip turn
 *
 * @param durationMillis animation duration in milliseconds
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
context(CoroutineScope)
suspend fun asIconAnimationAsync(
    durationMillis: Int,
    delayMillis: Int,
    update: (Float) -> Unit,
) = async {
    delay(delayMillis.toLong())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = produceIconKeyframes(durationMillis),
    ) { animatedValue, _ ->
        update(animatedValue)
    }
}
