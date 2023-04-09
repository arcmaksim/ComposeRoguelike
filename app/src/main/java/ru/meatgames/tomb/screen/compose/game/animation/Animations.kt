package ru.meatgames.tomb.screen.compose.game.animation

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.toIntOffset

private const val ANIMATION_DURATION = 300

internal val screenShakeKeyframes: AnimationSpec<Float> = keyframes {
    durationMillis = ANIMATION_DURATION
    val easing = FastOutLinearInEasing
    
    -1f at 60 with easing
    1f at 120 with easing
    -1f at 180 with easing
    1f at 240 with easing
    0f at 300 with easing
}

internal val attackKeyframes: AnimationSpec<Float> = keyframes {
    durationMillis = ANIMATION_DURATION
    val easing = FastOutLinearInEasing
    
    -1f at 150 with FastOutSlowInEasing
    2f at 250 with easing
    0f at 300 with easing
}

context(CoroutineScope)
suspend fun MutableState<IntOffset>.asDeferredOneDirectionAnimationAsync(
    animationSpec: AnimationSpec<Float>,
    direction: Direction,
) = async {
    val offset = direction.toIntOffset(10)
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = animationSpec,
    ) { animatedValue, _ ->
        value = IntOffset((offset.x * animatedValue).toInt(), (offset.y * animatedValue).toInt())
    }
}

context(CoroutineScope)
fun View.asDeferredRejectVibrationAsync() = async {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.REJECT)
    } else {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}

context(CoroutineScope)
fun View.asDeferredConfirmVibrationAsync(
    delay: Long = 0L,
) = async {
    delay(delay)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    } else {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}

suspend fun MutableState<MutableMap<EnemyId, IntOffset>>.asDeferredEnemiesAnimationAsync(
    scope: CoroutineScope,
    animationTimeInMillis: Int,
    enemyId: EnemyId,
    initialValue: IntOffset,
    targetValue: IntOffset,
) = scope.async {
    animate(
        initialValue = initialValue,
        targetValue = targetValue,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(animationTimeInMillis),
    ) { animatedValue, _ ->
        value[enemyId] = animatedValue
    }
}

suspend fun MutableState<MutableMap<EnemyId, IntOffset>>.asDeferredEnemiesAttackAnimationAsync(
    scope: CoroutineScope,
    tileDimension: Int,
    enemyId: EnemyId,
    direction: Direction,
) = scope.async {
    val offset = direction.toIntOffset((tileDimension * .6).toInt())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = attackKeyframes,
    ) { animatedValue, _ ->
        value[enemyId] = offset * animatedValue
    }
}
