package ru.meatgames.tomb.screen.compose.game.animation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.toIntOffset

fun List<Pair<EnemyId, EnemyAnimation>>.assembleEnemiesAnimations(
    scope: CoroutineScope,
    animationDurationMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState) -> Unit,
): Array<Deferred<Any>> = mapIndexedNotNull { index, (enemyId, animationState) ->
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipEnemiesAnimations)) return@mapIndexedNotNull null

    val delayMillis = animationDurationMillis / 3 * index

    when (animationState) {
        is EnemyAnimation.Move -> animationState.asAnimationAsync(
            scope = scope,
            enemyId = enemyId,
            durationMillis = animationDurationMillis,
            delayMillis = delayMillis,
            tileDimension = tileDimension,
            update = update,
        )

        is EnemyAnimation.Attack -> animationState.asAnimationAsync(
            scope = scope,
            enemyId = enemyId,
            delayMillis = delayMillis,
            tileDimension = tileDimension,
            update = update,
        )

        is EnemyAnimation.Icon -> animationState.asAnimationAsync(
            scope = scope,
            enemyId = enemyId,
            durationMillis = animationDurationMillis,
            delayMillis = delayMillis,
            update = update,
        )
    }
}.toTypedArray()

private fun EnemyAnimation.Move.asAnimationAsync(
    scope: CoroutineScope,
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): Deferred<Unit> = scope.enemiesMoveAnimationAsync(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    update = { coefficient ->
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
    },
)

private fun EnemyAnimation.Attack.asAnimationAsync(
    scope: CoroutineScope,
    enemyId: EnemyId,
    delayMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): Deferred<Unit> {
    val resoledExaggeration = ENEMIES_DEFAULT_ATTACK_EXAGGERATION.coerceIn(.5f, 2f)
    val animationDistance = tileDimension * ATTACK_DISTANCE_MODIFIER
    val offset = direction.toIntOffset((animationDistance * resoledExaggeration).toInt())

    return scope.enemiesAttackAnimationAsync(
        delayMillis = delayMillis,
        update = { coefficient ->
            update(
                enemyId,
                EnemyAnimationState.Transition(
                    offset = offset * coefficient,
                    alpha = 1f,
                ),
            )
        },
    )
}

private fun EnemyAnimation.Icon.asAnimationAsync(
    scope: CoroutineScope,
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    update: (EnemyId, EnemyAnimationState.Icon) -> Unit,
): Deferred<Unit> = scope.iconAnimationAsync(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    update = { alpha ->
        update(
            enemyId,
            EnemyAnimationState.Icon(
                renderData = renderData,
                iconAlpha = alpha,
            ),
        )
    },
)
