package ru.meatgames.tomb.screen.compose.game.component

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize

const val ANIMATION_FRAMES = 2
const val CHARACTER_IDLE_ANIMATION_TIME = 2

@Preview(widthDp = 270, heightDp = 270,)
@Composable
private fun GameScreenCharacterPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    
    val enemyType = EnemyType.SkeletonWarrior
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(240, 240),
        LocalHorizontalOffset provides IntOffset.Zero,
        LocalBackgroundColor provides Color(0xFF212121),
    ) {
        GameScreenCharacter(
            modifier = modifier,
            characterRenderData = themeAssets.getEnemyRenderData(enemyType),
            animationFrameTime = CHARACTER_IDLE_ANIMATION_TIME,
            viewportWidth = 3,
            viewportHeight = 3,
        )
    }
}

sealed class CharacterData {

    object Player : CharacterData()
    
    data class Enemy(
        val enemyType: EnemyType,
    ) : CharacterData()

}

@Composable
internal fun GameScreenCharacter(
    modifier: Modifier,
    characterRenderData: AnimationRenderData,
    animationFrameTime: Int,
    viewportWidth: Int,
    viewportHeight: Int,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    val infiniteTransition = rememberInfiniteTransition()
    
    val characterAnimationFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = ANIMATION_FRAMES,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = animationFrameTime * ANIMATION_FRAMES,
                easing = LinearEasing,
            ),
        )
    )
    
    Canvas(modifier = modifier) {
        drawCharacter(
            tileDimension = tileDimension,
            shadowRenderData = characterRenderData.shadowRenderData,
            frameIndex = characterAnimationFrame,
            characterRenderData = characterRenderData,
            dstSize = tileSize,
            dstOffset = offset + IntOffset(
                x = tileDimension * (viewportWidth / 2),
                y = tileDimension * (viewportHeight / 2),
            ),
        )
    }
}

