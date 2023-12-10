package ru.meatgames.tomb.presentation.tiles.animation

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.screen.game.animation.asFadeInAnimationAsync
import ru.meatgames.tomb.screen.game.animation.asFadeOutAnimationAsync

context(CoroutineScope)
suspend fun TilesAnimationState?.assembleTilesAnimations(
    animationDurationMillis: Int,
    fadeInAlpha: MutableState<Float>,
    fadeOutAlpha: MutableState<Float>,
): Array<Deferred<Any>> {
    val duration = if (FeatureToggles.getToggleValue(FeatureToggle.SkipTilesAnimations)) {
        0
    } else {
        animationDurationMillis
    }
    
    return listOf(
        fadeInAlpha.asFadeInAnimationAsync(duration),
        fadeOutAlpha.asFadeOutAnimationAsync(duration),
    ).toTypedArray()
}
