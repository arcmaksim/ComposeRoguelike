package ru.meatgames.tomb.screen.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.game.LocalTileSize
import ru.meatgames.tomb.screen.game.animation.EnemyAnimationState

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
        LocalBackgroundColor provides Color(0xFF212121),
    ) {
        GameScreenEnemies(
            modifier = modifier,
            tiles = gameScreenMapContainerPreviewRenderTiles(themeAssets),
            tilesWidth = gameScreenMapContainerPreviewMapSize,
            tilesPadding = 0,
            tilesToReveal = emptySet(),
            tilesToFade = emptySet(),
            animationStates = mutableMapOf(),
            initialOffset = IntOffset.Zero,
            animatedOffset = IntOffset.Zero,
            revealedTilesAlpha = 1f,
            fadedTilesAlpha = 0f,
            characterFrameIndex = 0,
        )
    }
}

@Composable
internal fun GameScreenEnemies(
    modifier: Modifier,
    tiles: List<MapRenderTile?>,
    tilesWidth: Int,
    tilesPadding: Int,
    tilesToReveal: Set<ScreenSpaceCoordinates>,
    tilesToFade: Set<ScreenSpaceCoordinates>,
    animationStates: Map<EnemyId, EnemyAnimationState?>,
    initialOffset: IntOffset,
    animatedOffset: IntOffset,
    revealedTilesAlpha: Float,
    fadedTilesAlpha: Float,
    characterFrameIndex: Int,
) {
    val tileSize = LocalTileSize.current
    val horizontalScreenBorderOffset = LocalHorizontalOffset.current
    val tileDimension = tileSize.width
    
    Canvas(modifier = modifier) {
        tiles.mapIndexedNotNull { index, mapRenderTile ->
            (mapRenderTile as? MapRenderTile.Content)
                ?.enemyData
                ?.enemyId
                ?.let { index to mapRenderTile }
        }.forEach { (index, renderTile) ->
            val tileScreenSpaceCoordinates = (index % tilesWidth - tilesPadding) to (index / tilesWidth - tilesPadding)
            val tileOffset = IntOffset(
                tileScreenSpaceCoordinates.first * tileDimension,
                tileScreenSpaceCoordinates.second * tileDimension,
            )
            
            val enemyId = renderTile.enemyData!!.enemyId
            
            val (animationOffset, animationAlpha) = animationStates[enemyId]
                ?.let { it as? EnemyAnimationState.Transition }
                ?.let { it.offset to it.alpha }
                ?: let { IntOffset.Zero to null }
            
            val iconState = animationStates[enemyId]?.let { it as? EnemyAnimationState.Icon }
            
            val dstOffset = horizontalScreenBorderOffset + initialOffset + animatedOffset + animationOffset + tileOffset
            
            when {
                animationAlpha != null -> animationAlpha
                renderTile.isVisible && tilesToReveal.contains(tileScreenSpaceCoordinates) -> revealedTilesAlpha
                renderTile.isVisible -> 1f
                tilesToFade.contains(tileScreenSpaceCoordinates) -> fadedTilesAlpha
                else -> null
            }?.let { alpha ->
                renderTile.enemyData.drawCharacter(
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    tileDimension = tileDimension,
                    alpha = alpha,
                    characterFrameIndex = characterFrameIndex,
                    iconState = iconState,
                )
            }
        }
    }
}

context(DrawScope)
private fun AnimationRenderData.drawCharacter(
    dstOffset: IntOffset,
    tileSize: IntSize,
    tileDimension: Int,
    characterFrameIndex: Int,
    alpha: Float?,
    iconState: EnemyAnimationState.Icon?,
) {
    drawCharacter(
        tileDimension = tileDimension,
        shadowRenderData = shadowRenderData,
        frameIndex = characterFrameIndex,
        characterRenderData = this,
        dstSize = tileSize,
        dstOffset = dstOffset,
        alpha = alpha ?: 1f,
        iconState = iconState,
    )
}
