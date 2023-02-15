package ru.meatgames.tomb.screen.compose.game.animation

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

private val keyframes: AnimationSpec<Float> = keyframes {
    durationMillis = 300
    val easing = FastOutLinearInEasing
    
    // generate 8 keyframes
    for (i in 1..8) {
        val x = when (i % 3) {
            0 -> 4f
            1 -> -4f
            else -> 0f
        }
        x at durationMillis / 10 * i with easing
    }
}

context(CoroutineScope)
suspend fun MutableState<Float>.asDeferredScreenShakeAnimationAsync() = async {
    animate(
        initialValue = 1f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = keyframes,
    ) { animatedValue, _ ->
        value = animatedValue
    }
}

context(CoroutineScope)
fun View.asDeferredVibrationAsync() = async {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.REJECT)
    } else {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
