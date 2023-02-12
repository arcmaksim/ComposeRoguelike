package ru.meatgames.tomb.screen.compose.game

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

context(CoroutineScope)
suspend fun getTilesValuesAnimation(
    animationTime: Int,
    revealedTileAlpha: MutableState<Float>,
    hiddenTileAlpha: MutableState<Float>,
): List<Deferred<Unit>> = listOf(
    async {
        animate(
            initialValue = 1f,
            targetValue = 0f,
            typeConverter = Float.VectorConverter,
            animationSpec = tween(
                durationMillis = animationTime,
            ),
        ) { value, _ ->
            revealedTileAlpha.value = value
        }
    },
    async {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            typeConverter = Float.VectorConverter,
            animationSpec = tween(
                durationMillis = animationTime,
            ),
        ) { value, _ ->
            hiddenTileAlpha.value = value
        }
    }
)