package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.config.Config
import ru.meatgames.tomb.domain.enemy.EnemyId

context(CoroutineScope)
suspend fun PlayerAnimationState?.assembleGameScreenAnimations(
    animationDurationMillis: Int,
    view: View,
    shakeOffset: MutableState<IntOffset>,
    animatedOffset: MutableState<IntOffset>,
    initialAnimatedOffset: IntOffset,
    revealedTilesAlpha: MutableState<Float>,
    fadedTilesAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    if (Config.skipPlayerAnimations) return emptyArray()
    
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
                    durationMillis = animationDurationMillis,
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
            revealedTilesAlpha.asDeferredRevealAnimationAsync(animationDurationMillis),
            fadedTilesAlpha.asDeferredFadeAnimationAsync(animationDurationMillis),
        )
    }
    
    return (specificAnimations + tilesAnimations).toTypedArray()
}

suspend fun List<Pair<EnemyId, EnemiesAnimationState>>.assembleEnemiesAnimations(
    scope: CoroutineScope,
    animationDurationMillis: Int,
    tileDimension: Int,
    update: (EnemyId, IntOffset) -> Unit,
): Array<Deferred<Any>> = mapIndexedNotNull { index, (enemyId, animationState) ->
    if (Config.skipEnemiesAnimations) return@mapIndexedNotNull null
    
    val delayMillis = animationDurationMillis / 2 * index
    when (animationState) {
        is EnemiesAnimationState.Move -> {
            asDeferredEnemiesMoveAnimationAsync(
                scope = scope,
                tileDimension = tileDimension,
                durationMillis = animationDurationMillis,
                delayMillis = delayMillis,
                direction = animationState.direction,
                enemyId = enemyId,
                update = update,
            )
        }
    
        is EnemiesAnimationState.Attack -> {
            asDeferredEnemiesAttackAnimationAsync(
                scope = scope,
                tileDimension = tileDimension,
                delayMillis = delayMillis,
                direction = animationState.direction,
                enemyId = enemyId,
                update = update,
            )
        }
    }
}.toTypedArray()
