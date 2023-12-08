package ru.meatgames.tomb.screen.game.animation

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.presentation.enemies.EnemyAnimation
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.toIntOffset

/*context(CoroutineScope)
suspend fun List<Pair<EnemyId, EnemyAnimation>>.assembleEnemiesAnimations(
    animationDurationMillis: Int,
    tileDimension: Int,
    view: View,
    onAttack: () -> Unit,
    update: (EnemyId, EnemyAnimationState) -> Unit,
): Array<Deferred<Any>> {
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipEnemiesAnimations)) return emptyArray()
    return mapIndexedNotNull { index, (enemyId, animationState) ->
        val delayMillis = animationDurationMillis / 3 * index
        
        when (animationState) {
            is EnemyAnimation.Move -> animationState.asAnimationAsync(
                enemyId = enemyId,
                durationMillis = animationDurationMillis,
                delayMillis = delayMillis,
                tileDimension = tileDimension,
                update = update,
            )
            
            is EnemyAnimation.Attack -> animationState.asAnimationAsync2(
                enemyId = enemyId,
                delayMillis = delayMillis,
                tileDimension = tileDimension,
                update = update,
                view = view,
                onAttack = onAttack,
            )
            
            is EnemyAnimation.Icon -> animationState.asAnimationAsync(
                enemyId = enemyId,
                durationMillis = animationDurationMillis,
                delayMillis = delayMillis,
                update = update,
            )
        }
    }.toTypedArray()
}*/

sealed class EnemyAnimationEvent {
    
    data class Attack(
        val enemyId: EnemyId,
    ) : EnemyAnimationEvent()
    
}

context(CoroutineScope)
suspend fun List<Pair<EnemyId, EnemyAnimation>>.assembleEnemiesAnimations2(
    animationDurationMillis: Int,
    tileDimension: Int,
    view: View,
    onAttack: (EnemyAnimationEvent) -> Unit,
    update: (EnemyId, EnemyAnimationState) -> Unit,
): Array<Deferred<Any>> {
    if (FeatureToggles.getToggleValue(FeatureToggle.SkipEnemiesAnimations)) return emptyArray()
    return mapIndexed { index, (enemyId, animationState) ->
        val delayMillis = animationDurationMillis / 3 * index
        
        when (animationState) {
            is EnemyAnimation.Move -> listOf(animationState.asAnimationAsync(
                enemyId = enemyId,
                durationMillis = animationDurationMillis,
                delayMillis = delayMillis,
                tileDimension = tileDimension,
                update = update,
            ))
            
            is EnemyAnimation.Attack -> animationState.asAnimationAsync2(
                enemyId = enemyId,
                delayMillis = delayMillis,
                tileDimension = tileDimension,
                update = update,
                view = view,
                onAttack = onAttack,
            )
            
            is EnemyAnimation.Icon -> listOf(animationState.asAnimationAsync(
                enemyId = enemyId,
                durationMillis = animationDurationMillis,
                delayMillis = delayMillis,
                update = update,
            ))
        }
    }.fold(mutableListOf<Deferred<Any>>()) { acc, deferred -> acc.also { it.addAll(deferred) } }
        .toTypedArray()
}

context(CoroutineScope)
private suspend fun EnemyAnimation.Move.asAnimationAsync(
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    tileDimension: Int,
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): Deferred<Any> = asEnemiesMoveAnimationAsync(
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
private suspend fun EnemyAnimation.Attack.asAnimationAsync2(
    enemyId: EnemyId,
    delayMillis: Int,
    tileDimension: Int,
    view: View,
    onAttack: (EnemyAnimationEvent) -> Unit = {},
    update: (EnemyId, EnemyAnimationState.Transition) -> Unit,
): List<Deferred<Any>> {
    val resoledExaggeration = ENEMIES_DEFAULT_ATTACK_EXAGGERATION.coerceIn(.5f, 2f)
    val animationDistance = tileDimension * ATTACK_DISTANCE_MODIFIER
    val offset = direction.toIntOffset((animationDistance * resoledExaggeration).toInt())
    
    val attackAnimation = asEnemiesAttackAnimationAsync(
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
    val vibration = view.asDeferredConfirmVibrationAsync(
        delayMillis = DEFAULT_ATTACK_DELAY_MILLIS,
        onAnimation = {
            logMessage("Attack", "Получай пизды блять")
            onAttack(EnemyAnimationEvent.Attack(enemyId)) },
    )
    
    return listOf(
        attackAnimation,
        vibration,
    )
}

context(CoroutineScope)
private suspend fun EnemyAnimation.Icon.asAnimationAsync(
    enemyId: EnemyId,
    durationMillis: Int,
    delayMillis: Int,
    update: (EnemyId, EnemyAnimationState.Icon) -> Unit,
): Deferred<Any> {
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
