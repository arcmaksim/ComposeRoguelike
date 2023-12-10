package ru.meatgames.tomb.screen.game.component

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.backgroundColor
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.domain.enemy.produceEnemy
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.presentation.render.model.AnimationRenderData
import ru.meatgames.tomb.screen.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.game.LocalTileSize
import ru.meatgames.tomb.screen.game.animation.CHARACTER_IDLE_ANIMATION_DURATION_MILLIS
import ru.meatgames.tomb.screen.game.animation.CHARACTER_IDLE_ANIMATION_FRAMES

/**
 * There are bunch of magic numbers just to make preview look nice
 */
@Preview(widthDp = 140, heightDp = 140)
@Composable
private fun GameScreenCharacterPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    val enemyType = EnemyType.SkeletonWarrior.produceEnemy(0 to 0)
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(240, 240),
        LocalHorizontalOffset provides IntOffset.Zero,
        LocalBackgroundColor provides backgroundColor,
    ) {
        GameScreenCharacter(
            modifier = modifier.offset(24.dp, 40.dp),
            characterRenderData = themeAssets.getEnemyRenderData(enemyType),
            viewportWidth = 1,
            viewportHeight = 1,
        )
    }
}

@Composable
internal fun GameScreenCharacter(
    modifier: Modifier,
    characterRenderData: AnimationRenderData,
    viewportWidth: Int,
    viewportHeight: Int,
) {
    val characterIdleTransition = rememberInfiniteTransition(label = "Character idle infinite transition")
    val characterAnimationFrameIndex by characterIdleTransition.produceCharacterAnimation()
    
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    Canvas(modifier = modifier) {
        drawCharacter(
            tileDimension = tileDimension,
            shadowRenderData = characterRenderData.shadowRenderData,
            frameIndex = characterAnimationFrameIndex,
            characterRenderData = characterRenderData,
            dstSize = tileSize,
            dstOffset = offset + IntOffset(
                x = tileDimension * (viewportWidth / 2),
                y = tileDimension * (viewportHeight / 2),
            ),
            alpha = 1f,
            iconState = null,
        )
    }
}

@Composable
private fun InfiniteTransition.produceCharacterAnimation(): State<Int> {
    return animateValue(
        label = "Character idle animation frame index",
        initialValue = 0,
        targetValue = CHARACTER_IDLE_ANIMATION_FRAMES,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = CHARACTER_IDLE_ANIMATION_DURATION_MILLIS * CHARACTER_IDLE_ANIMATION_FRAMES,
                easing = LinearEasing,
            ),
        ),
    )
}

