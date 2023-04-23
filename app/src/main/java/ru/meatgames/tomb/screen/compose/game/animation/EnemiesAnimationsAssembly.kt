package ru.meatgames.tomb.screen.compose.game.animation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggleKey
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.toIntOffset

context(CoroutineScope)
suspend fun List<Pair<EnemyId, EnemyAnimation>>.assembleEnemiesAnimations(
    animationDurationMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState) -> Unit,
): Array<Deferred<Any>> = mapIndexedNotNull { index, (enemyId, animationState) ->
    if (FeatureToggles.getToggleValue(FeatureToggleKey.SkipEnemiesAnimations)) return@mapIndexedNotNull null
    
    val delayMillis = animationDurationMillis / 3 * index
    
    when (animationState) {
        is EnemyAnimation.Move -> animationState.asAnimationAsync(
            enemyId = enemyId,
            durationMillis = animationDurationMillis,
            delayMillis = delayMillis,
            tileDimension = tileDimension,
            update = update,
        )
        
        is EnemyAnimation.Attack -> animationState.asAnimationAsync(
            enemyId = enemyId,
            delayMillis = delayMillis,
            tileDimension = tileDimension,
            update = update,
        )
        
        is EnemyAnimation.Icon -> animationState.asAnimationAsync(
            enemyId = enemyId,
            durationMillis = animationDurationMillis,
            delayMillis = delayMillis,
            update = update,
        )
    }
}.toTypedArray()

context(CoroutineScope)
private suspend fun EnemyAnimation.Move.asAnimationAsync(
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): Deferred<Unit> = asEnemiesMoveAnimationAsync(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
) { coefficient ->
    update(
        enemyId,
        EnemyAnimationState.Transition(
            offset = -direction.toIntOffset(tileDimension) * coefficient,
            alpha = when (fade) {
                EnemyAnimation.Move.Fade.IN -> 1f - coefficient
                EnemyAnimation.Move.Fade.OUT -> coefficient
                EnemyAnimation.Move.Fade.NONE -> 1f
            },
        ),
    )
}

context(CoroutineScope)
private suspend fun EnemyAnimation.Attack.asAnimationAsync(
    enemyId: EnemyId,
    delayMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): Deferred<Unit> {
    val resoledExaggeration = ENEMIES_DEFAULT_ATTACK_EXAGGERATION.coerceIn(.5f, 2f)
    val animationDistance = tileDimension * ATTACK_DISTANCE_MODIFIER
    val offset = direction.toIntOffset((animationDistance * resoledExaggeration).toInt())
    
    return asEnemiesAttackAnimationAsync(
        delayMillis = delayMillis,
    ) { coefficient ->
        update(
            enemyId,
            EnemyAnimationState.Transition(
                offset = offset * coefficient,
                alpha = 1f,
            ),
        )
    }
}

context(CoroutineScope)
private suspend fun EnemyAnimation.Icon.asAnimationAsync(
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    update: (EnemyId, EnemyAnimationState.Icon) -> Unit,
): Deferred<Unit> {
    return asIconAnimationAsync(
        durationMillis = durationMillis,
        delayMillis = delayMillis,
    ) { alpha ->
        update(
            enemyId,
            EnemyAnimationState.Icon(
                renderData = renderData,
                iconAlpha = alpha,
            ),
        )
    }
}
