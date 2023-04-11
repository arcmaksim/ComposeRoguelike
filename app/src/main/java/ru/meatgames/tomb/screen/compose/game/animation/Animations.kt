package ru.meatgames.tomb.screen.compose.game.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
 * Extension function for [MutableState] of [IntOffset] to animate enemies movement
 *
 * @param tileDimension tile dimension in pixels (after resolving with screen density)
 * @param durationInMillis animation time in milliseconds
 * @param enemyId key for putting animated value to the map
 * @param direction direction of the animation
 */
suspend fun MutableState<MutableMap<EnemyId, IntOffset>>.asDeferredEnemiesMoveAnimationAsync(
    scope: CoroutineScope,
    tileDimension: Int,
    durationInMillis: Int,
    enemyId: EnemyId,
    direction: Direction,
) = scope.async {
    animate(
        initialValue = -direction.toIntOffset(tileDimension),
        targetValue = IntOffset.Zero,
        typeConverter = IntOffset.VectorConverter,
        animationSpec = tween(durationInMillis),
    ) { animatedValue, _ ->
        value[enemyId] = animatedValue
    }
}

/**
 * Extension function for [MutableState] of [MutableMap] of [EnemyId] and [IntOffset] to animate enemies attack
 *
 * @param tileDimension tile dimension in pixels (after resolving with screen density)
 * @param enemyId key for putting animated value to the map
 * @param direction direction of the animation
 * @param exaggeration animation scale, can be set between .5 and 2
 */
suspend fun MutableState<MutableMap<EnemyId, IntOffset>>.asDeferredEnemiesAttackAnimationAsync(
    scope: CoroutineScope,
    tileDimension: Int,
    enemyId: EnemyId,
    direction: Direction,
    exaggeration: Float = ENEMIES_DEFAULT_ATTACK_EXAGGERATION,
) = scope.async {
    val resoledExaggeration = exaggeration.coerceIn(.5f, 2f)
    val animationDistance = tileDimension * ATTACK_DISTANCE_MODIFIER
    val offset = direction.toIntOffset((animationDistance * resoledExaggeration).toInt())
    animate(
        initialValue = 0f,
        targetValue = 0f,
        typeConverter = Float.VectorConverter,
        animationSpec = defaultAttackKeyframes,
    ) { animatedValue, _ ->
        value[enemyId] = offset * animatedValue
    }
}
