package ru.meatgames.tomb.screen.compose.game.component

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
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize

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
        GameScreenMap(
            modifier = modifier,
            tiles = gameScreenMapContainerPreviewRenderTiles(themeAssets),
            tilesWidth = gameScreenMapContainerPreviewMapSize,
            tilesPadding = 0,
            tilesToReveal = emptySet(),
            tilesToFade = emptySet(),
            initialOffset = IntOffset.Zero,
            animatedOffset = IntOffset.Zero,
            revealedTilesAlpha = 1f,
            fadedTilesAlpha = 0f,
        )
    }
}

@Composable
internal fun GameScreenMap(
    modifier: Modifier,
    tiles: List<MapRenderTile?>,
    tilesWidth: Int,
    tilesPadding: Int,
    tilesToReveal: Set<ScreenSpaceCoordinates>,
    tilesToFade: Set<ScreenSpaceCoordinates>,
    initialOffset: IntOffset,
    animatedOffset: IntOffset,
    revealedTilesAlpha: Float,
    fadedTilesAlpha: Float,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = tileSize.width
    val backgroundColor = LocalBackgroundColor.current
    
    Canvas(modifier = modifier) {
        tiles.forEachIndexed { index, renderTile ->
            val tileScreenSpaceCoordinates = (index % tilesWidth - tilesPadding) to (index / tilesWidth - tilesPadding)
            val tileOffset = IntOffset(
                tileScreenSpaceCoordinates.first * tileDimension,
                tileScreenSpaceCoordinates.second * tileDimension,
            )
            
            val dstOffset = offset + initialOffset + animatedOffset + tileOffset
            
            when {
                renderTile !is MapRenderTile.Content -> null
                renderTile.isVisible && tilesToReveal.contains(tileScreenSpaceCoordinates) -> {
                    if (renderTile.objectData != null) logMessage("GameScreenMap", "flag1: $revealedTilesAlpha")
                    renderTile to revealedTilesAlpha
                }
                renderTile.isVisible -> {
                    if (renderTile.objectData != null) logMessage("GameScreenMap", "flag2: ${1f}")
                    renderTile to 1f
                }
                tilesToFade.contains(tileScreenSpaceCoordinates) -> renderTile to fadedTilesAlpha
                else -> null
            }?.let { (tile, alpha) ->
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
