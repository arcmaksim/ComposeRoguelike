package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
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
fun CoroutineScope.directionalKeyframeIntOffsetAnimationAsync(
    keyframesSpec: KeyframesSpec<Float>,
    direction: Direction,
    onChange: (IntOffset) -> Unit,
) = async {
    val offset = direction.toIntOffset(10)
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = keyframesSpec,
        block = { animatedValue, _ ->
            onChange(
                IntOffset(
                    (offset.x * animatedValue).toInt(),
                    (offset.y * animatedValue).toInt(),
                ),
            )
        },
    )
}

/**
 * Animate enemy movement
 *
 * @param durationMillis animation time in milliseconds
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
fun CoroutineScope.enemiesMoveAnimationAsync(
    durationMillis: Int,
    delayMillis: Int = 0,
    update: (Float) -> Unit,
) = async {
    animate(
        initialValue = 1f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis),
        block = { animatedValue, _ ->
            update(animatedValue)
        },
    )
}

/**
 * Animate enemy attack
 *
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
fun CoroutineScope.enemiesAttackAnimationAsync(
    delayMillis: Int,
    update: (Float) -> Unit,
) = async {
    delay(delayMillis.toLong())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = defaultAttackKeyframes,
        block = { animatedValue, _ ->
            update(animatedValue)
        },
    )
}

/**
 * Animate skip turn
 *
 * @param durationMillis animation duration in milliseconds
 * @param delayMillis animation delay in milliseconds
 * @param update callback
 */
fun CoroutineScope.iconAnimationAsync(
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
        block = { animatedValue, _ ->
            update(animatedValue)
        },
    )
}
