package ru.meatgames.tomb.screen.game.animation

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

context(CoroutineScope)
fun View.asDeferredRejectVibrationAsync() = async {
    performHapticFeedback(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.REJECT
        } else {
            HapticFeedbackConstants.LONG_PRESS
        },
    )
}

context(CoroutineScope)
fun View.asDeferredConfirmVibrationAsync(
    delayMillis: Int = 0,
    onAnimation: () -> Unit = {},
) = async {
    delay(delayMillis.toLong())
    onAnimation()
    performHapticFeedback(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.LONG_PRESS
        },
    )
}
