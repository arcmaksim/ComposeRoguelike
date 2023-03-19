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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.render.CharacterIdleAnimationDirection
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize

@Preview(widthDp = 270, heightDp = 270,)
@Composable
private fun GameScreenCharacterPreview() {
    val context = LocalContext.current
    
    NewAssets.loadAssets(context)
    
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
            heroAnimationFrames = 2,
            heroAnimationFrameTime = 600,
            viewportWidth = 3,
            viewportHeight = 3,
            idleAnimationDirection = CharacterIdleAnimationDirection.Left,
        )
    }
}

@Composable
internal fun GameScreenCharacter(
    modifier: Modifier,
    heroAnimationFrames: Int,
    heroAnimationFrameTime: Int,
    viewportWidth: Int,
    viewportHeight: Int,
    idleAnimationDirection: CharacterIdleAnimationDirection = CharacterIdleAnimationDirection.Left,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    val infiniteTransition = rememberInfiniteTransition()
    
    val heroAnimationDirectionModifier by remember {
        mutableStateOf(
            when (idleAnimationDirection) {
                CharacterIdleAnimationDirection.Left -> 0
                CharacterIdleAnimationDirection.Right -> 2
            }
        )
    }
    
    val heroAnimationFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = heroAnimationFrames,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = heroAnimationFrameTime * heroAnimationFrames,
                easing = LinearEasing,
            ),
        )
    )
    
    val resultHeroFrame by remember {
        derivedStateOf { heroAnimationDirectionModifier + heroAnimationFrame }
    }
    
    Canvas(modifier = modifier) {
        drawImage(
            image = NewAssets.getHeroBitmap(resultHeroFrame),
            dstOffset = offset + IntOffset(
                x = tileDimension * (viewportWidth / 2),
                y = tileDimension * (viewportHeight / 2)
            ),
            dstSize = tileSize,
            filterQuality = FilterQuality.None,
        )
    }
}
