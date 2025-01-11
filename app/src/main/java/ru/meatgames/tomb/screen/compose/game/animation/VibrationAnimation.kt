package ru.meatgames.tomb.screen.compose.game.animation

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

fun CoroutineScope.rejectVibrationAsync(
    view: View,
) = async {
    view.performHapticFeedback(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.REJECT
        } else {
            HapticFeedbackConstants.LONG_PRESS
        },
    )
}

fun CoroutineScope.confirmVibrationAsync(
    view: View,
    delay: Long = 0L,
) = async {
    delay(delay)
    view.performHapticFeedback(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.LONG_PRESS
        },
    )
}
