package ru.meatgames.tomb.screen.game.animation

import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.presentation.enemies.EnemyAnimation
import ru.meatgames.tomb.presentation.render.model.RenderData
import ru.meatgames.tomb.toIntOffset

sealed class EnemyAnimationState {
    
    data class Transition(
        val offset: IntOffset,
        val alpha: Float,
    ) : EnemyAnimationState() {
        constructor(
            moveState: EnemyAnimation.Move,
            tileDimension: Int,
        ) : this(
            offset = -moveState.direction.toIntOffset(tileDimension),
            alpha = when (moveState.fade) {
                EnemyAnimation.Move.Fade.IN -> 0f
                EnemyAnimation.Move.Fade.OUT -> 1f
                EnemyAnimation.Move.Fade.NONE -> 1f
            },
        )
    }
    
    data class Icon(
        val renderData: RenderData,
        val iconAlpha: Float,
    ) : EnemyAnimationState()
    
}
