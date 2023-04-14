package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.toIntOffset

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

/**
 * Animate enemy movement
 *
 * @param scope coroutine scope
 * @param tileDimension tile dimension in pixels (after resolving with screen density)
 * @param durationMillis animation time in milliseconds
 * @param delayMillis animation delay in milliseconds
 * @param enemyId key for putting animated value to the map
 * @param direction direction of the animation
 * @param update callback
 */
suspend fun asDeferredEnemiesMoveAnimationAsync(
    scope: CoroutineScope,
    tileDimension: Int,
    durationMillis: Int,
    delayMillis: Int = 0,
    enemyId: EnemyId,
    direction: Direction,
    update: (EnemyId, IntOffset) -> Unit,
) = scope.async {
    animate(
        initialValue = -direction.toIntOffset(tileDimension),
        targetValue = IntOffset.Zero,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis),
    ) { animatedValue, _ ->
        update(enemyId, animatedValue)
    }
}

/**
 * Animate enemy attack
 *
 * @param scope coroutine scope
 * @param tileDimension tile dimension in pixels (after resolving with screen density)
 * @param delayMillis animation delay in milliseconds
 * @param enemyId key for putting animated value to the map
 * @param direction direction of the animation
 * @param exaggeration animation scale, can be set between .5 and 2
 * @param update callback
 */
suspend fun asDeferredEnemiesAttackAnimationAsync(
    scope: CoroutineScope,
    tileDimension: Int,
    delayMillis: Int,
    enemyId: EnemyId,
    direction: Direction,
    exaggeration: Float = ENEMIES_DEFAULT_ATTACK_EXAGGERATION,
    update: (EnemyId, IntOffset) -> Unit,
) = scope.async {
    val resoledExaggeration = exaggeration.coerceIn(.5f, 2f)
    val animationDistance = tileDimension * ATTACK_DISTANCE_MODIFIER
    val offset = direction.toIntOffset((animationDistance * resoledExaggeration).toInt())
    
    delay(delayMillis.toLong())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = defaultAttackKeyframes,
    ) { animatedValue, _ ->
        update(enemyId, offset * animatedValue)
    }
}
