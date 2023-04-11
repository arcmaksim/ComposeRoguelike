package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.enemy.EnemyId

context(CoroutineScope)
suspend fun PlayerAnimationState?.assembleGameScreenAnimations(
    animationTime: Int,
    view: View,
    shakeOffset: MutableState<IntOffset>,
    animatedOffset: MutableState<IntOffset>,
    initialAnimatedOffset: IntOffset,
    revealedTilesAlpha: MutableState<Float>,
    fadedTilesAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    val specificAnimations = when (this) {
        is PlayerAnimationState.Shake -> {
            listOf(
                shakeOffset.asDeferredOneDirectionAnimationAsync(
                    screenShakeKeyframes,
                    Direction.Right,
                ),
                view.asDeferredRejectVibrationAsync(),
            )
        }
        
        is PlayerAnimationState.Move -> {
            listOf(
                animatedOffset.asDeferredMoveAnimationAsync(
                    animationTime = animationTime,
                    targetValue = initialAnimatedOffset,
                ),
            )
        }
        
        is PlayerAnimationState.Attack -> {
            listOf(
                shakeOffset.asDeferredOneDirectionAnimationAsync(
                    defaultAttackKeyframes,
                    direction,
                ),
                view.asDeferredConfirmVibrationAsync(250L),
            )
        }
        
        else -> emptyList()
    }
    
    val tilesAnimations = if (isStateless) {
        emptyList<Deferred<Any>>()
    } else {
        listOf(
            revealedTilesAlpha.asDeferredRevealAnimationAsync(animationTime),
            fadedTilesAlpha.asDeferredFadeAnimationAsync(animationTime),
        )
    }
    
    return (specificAnimations + tilesAnimations).toTypedArray()
}

suspend fun List<Pair<EnemyId, EnemiesAnimationState>>.assembleEnemiesAnimations(
    scope: CoroutineScope,
    animationTime: Int,
    tileDimension: Int,
    animatedState: MutableState<MutableMap<EnemyId, IntOffset>>,
): Array<Deferred<Any>> = map { (enemyId, animationState) ->
    when (animationState) {
        is EnemiesAnimationState.Move -> {
            animatedState.asDeferredEnemiesMoveAnimationAsync(
                scope = scope,
                tileDimension = tileDimension,
                durationInMillis = animationTime,
                direction = animationState.direction,
                enemyId = enemyId,
            )
        }
        
        is EnemiesAnimationState.Attack -> {
            animatedState.asDeferredEnemiesAttackAnimationAsync(
                scope = scope,
                tileDimension = tileDimension,
                direction = animationState.direction,
                enemyId = enemyId,
            )
        }
    }
}.toTypedArray()
