package ru.meatgames.tomb.screen.compose.game.animation

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.model.temp.ASSETS_TILE_DIMENSION
import ru.meatgames.tomb.toIntOffset

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
        
        is PlayerAnimationState.Scroll -> {
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
                    attackKeyframes,
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
    dimension: Int,
    animatedState: MutableState<MutableMap<EnemyId, IntOffset>>,
): Array<Deferred<Any>> = map { (enemyId, animationState) ->
    when (animationState) {
        is EnemiesAnimationState.Move -> {
            animatedState.asDeferredEnemiesAnimationAsync(
                scope = scope,
                animationTimeInMillis = animationTime,
                initialValue = -animationState.direction.toIntOffset(dimension),
                targetValue = IntOffset.Zero,
                enemyId = enemyId,
            )
        }
        
        is EnemiesAnimationState.Attack -> {
            animatedState.asDeferredEnemiesAttackAnimationAsync(
                scope = scope,
                tileDimension = ASSETS_TILE_DIMENSION,
                //animationTimeInMillis = animationTime,
                direction = animationState.direction,
                enemyId = enemyId,
            )
        }
    }
}.toTypedArray()
