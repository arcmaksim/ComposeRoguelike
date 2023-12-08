package ru.meatgames.tomb.presentation.camera.animation

import androidx.compose.ui.unit.IntOffset

/**
 * @param creationTime is needed to always produce new hash code
 * for each new animation of the same type,
 * and have automatic hash code generation out of the box.
 */
sealed class CameraAnimationState {
    
    data class Smooth(
        val offset: IntOffset,
        private val creationTime: Long,
    ) : CameraAnimationState()
    
    data class Instant(
        private val creationTime: Long,
    ) : CameraAnimationState()
    
}