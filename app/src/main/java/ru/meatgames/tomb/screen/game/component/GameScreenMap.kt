package ru.meatgames.tomb.screen.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.awaitAll
import ru.meatgames.tomb.design.backgroundColor
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.presentation.render.model.MapRenderTile
import ru.meatgames.tomb.presentation.tiles.animation.TilesAnimationState
import ru.meatgames.tomb.presentation.tiles.animation.assembleTilesAnimations
import ru.meatgames.tomb.screen.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.game.LocalTileSize

@Preview(widthDp = 436, heightDp = 436)
@Composable
private fun GameScreenMapPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(240, 240),
        LocalHorizontalOffset provides IntOffset.Zero,
        LocalBackgroundColor provides backgroundColor,
    ) {
        GameScreenMap(
            modifier = modifier,
            tiles = gameScreenMapContainerPreviewRenderTiles(themeAssets),
            tilesWidth = gameScreenMapContainerPreviewMapSize,
            tilesPadding = 0,
            animationDurationMillis = 0,
            tilesAnimation = TilesAnimationState(
                creationTime = 0L,
                tilesToFadeIn = emptySet(),
                tilesToFadeOut = emptySet(),
            ),
            initialOffset = IntOffset.Zero,
            animatedOffset = IntOffset.Zero,
        )
    }
}

@Composable
internal fun GameScreenMap(
    modifier: Modifier,
    tiles: List<MapRenderTile?>,
    tilesWidth: Int,
    tilesPadding: Int,
    animationDurationMillis: Int,
    tilesAnimation: TilesAnimationState?,
    initialOffset: IntOffset,
    animatedOffset: IntOffset,
) {
    var playedTilesAnimation by rememberSaveable { mutableIntStateOf(-1) }
   
    val fadeInAlpha = remember(tilesAnimation) {
        mutableFloatStateOf(if (tilesAnimation.hashCode() == playedTilesAnimation) 1f else 0f)
    }
    val fadeOutAlpha = remember(tilesAnimation) {
        mutableFloatStateOf(if (tilesAnimation.hashCode() == playedTilesAnimation) 0f else 1f)
    }
    
    LaunchedEffect(tilesAnimation) {
        if (tilesAnimation == null) {
            playedTilesAnimation = -1
            return@LaunchedEffect
        }
        
        val currentAnimation = tilesAnimation.hashCode()
        if (currentAnimation == playedTilesAnimation) return@LaunchedEffect
        
        playedTilesAnimation = currentAnimation
        awaitAll(
            *tilesAnimation.assembleTilesAnimations(
                animationDurationMillis = animationDurationMillis,
                fadeInAlpha = fadeInAlpha,
                fadeOutAlpha = fadeOutAlpha,
            ),
        )
    }
    
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = tileSize.width
    val backgroundColor = LocalBackgroundColor.current
    
    val baseOffset = remember(offset, initialOffset, animatedOffset) {
        offset + initialOffset + animatedOffset
    }
    
    Canvas(modifier = modifier) {
        tiles.forEachIndexed { index, renderTile ->
            val tileScreenSpaceCoordinates =
                (index % tilesWidth - tilesPadding) to (index / tilesWidth - tilesPadding)
            val tileOffset = IntOffset(
                tileScreenSpaceCoordinates.first * tileDimension,
                tileScreenSpaceCoordinates.second * tileDimension,
            )
            
            val dstOffset = baseOffset + tileOffset
            
            renderTile?.resolveRenderState(
                tileScreenSpaceCoordinates = tileScreenSpaceCoordinates,
                tilesAnimation = tilesAnimation,
                fadeInAlpha = fadeInAlpha.floatValue,
                fadeOutAlpha = fadeOutAlpha.floatValue,
            )?.let { (tile, alpha) ->
                tile.drawRevealedTile(
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    backgroundColor = backgroundColor,
                    alpha = alpha,
                )
            }
        }
    }
}

private fun MapRenderTile.resolveRenderState(
    tileScreenSpaceCoordinates: Pair<Int, Int>,
    tilesAnimation: TilesAnimationState?,
    fadeInAlpha: Float,
    fadeOutAlpha: Float,
): Pair<MapRenderTile.Content, Float>? = when {
    this !is MapRenderTile.Content -> {
        null
    }
    
    isVisible && tilesAnimation?.tilesToFadeIn?.contains(tileScreenSpaceCoordinates) == true -> {
        this to fadeInAlpha
    }
    
    isVisible -> {
        this to 1f
    }
    
    tilesAnimation?.tilesToFadeOut?.contains(tileScreenSpaceCoordinates) == true -> {
        this to fadeOutAlpha
    }
    
    else -> null
}

context(DrawScope)
private fun MapRenderTile.Content.drawRevealedTile(
    dstOffset: IntOffset,
    tileSize: IntSize,
    backgroundColor: Color,
    alpha: Float = 1f,
) {
    floorData.drawImage(
        dstOffset = dstOffset,
        dstSize = tileSize,
    )
    decorations.forEach {
        it.drawImage(
            dstOffset = dstOffset,
            dstSize = tileSize,
        )
    }
    objectData?.drawImage(
        dstOffset = dstOffset,
        dstSize = tileSize,
    )
    itemData?.drawImage(
        dstOffset = dstOffset,
        dstSize = tileSize,
    )
    if (alpha != 1f) {
        drawRect(
            color = backgroundColor,
            topLeft = dstOffset.toOffset(),
            size = tileSize.toSize(),
            alpha = 1f - alpha,
        )
    }
}