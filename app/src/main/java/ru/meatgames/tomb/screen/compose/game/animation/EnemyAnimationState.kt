package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.toIntOffset

data class EnemyAnimationState(
    val offset: IntOffset,
    val alpha: Float,
) {
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
