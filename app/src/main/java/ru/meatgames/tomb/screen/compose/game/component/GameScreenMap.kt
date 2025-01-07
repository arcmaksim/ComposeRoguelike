package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
import ru.meatgames.tomb.model.theme.ThemeAssets
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
    
    val baseOffset = remember(offset, initialOffset, animatedOffset) {
        offset + initialOffset + animatedOffset
    }
    
    Canvas(modifier = modifier) {
        tiles.forEachIndexed { index, renderTile ->
            val tileScreenSpaceCoordinates = (index % tilesWidth - tilesPadding) to (index / tilesWidth - tilesPadding)
            val tileOffset = IntOffset(
                tileScreenSpaceCoordinates.first * tileDimension,
                tileScreenSpaceCoordinates.second * tileDimension,
            )
            
            val dstOffset = baseOffset + tileOffset
            
            when {
                renderTile !is MapRenderTile.Content -> {
                    null
                }
                renderTile.isVisible && tilesToReveal.contains(tileScreenSpaceCoordinates) -> {
                    renderTile to revealedTilesAlpha
                }
                renderTile.isVisible -> {
                    renderTile to 1f
                }
                tilesToFade.contains(tileScreenSpaceCoordinates) -> {
                    renderTile to fadedTilesAlpha
                }
                else -> null
            }?.let { (tile, alpha) ->
                drawRevealedTile(
                    tile = tile,
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    backgroundColor = backgroundColor,
                    alpha = alpha,
                )
            }
        }
    }
}

private fun DrawScope.drawRevealedTile(
    tile: MapRenderTile.Content,
    dstOffset: IntOffset,
    tileSize: IntSize,
    backgroundColor: Color,
    alpha: Float = 1f,
) {
    drawImage(
        renderData = tile.floorData,
        dstOffset = dstOffset,
        dstSize = tileSize,
    )
    tile.decorations.forEach {
        drawImage(
            renderData = it,
            dstOffset = dstOffset,
            dstSize = tileSize,
        )
    }
    tile.objectData?.let {
        drawImage(
            renderData = it,
            dstOffset = dstOffset,
            dstSize = tileSize,
        )
    }
    tile.itemData?.let {
        drawImage(
            renderData = it,
            dstOffset = dstOffset,
            dstSize = tileSize,
        )
    }
    if (alpha != 1f) {
        drawRect(
            color = backgroundColor,
            topLeft = dstOffset.toOffset(),
            size = tileSize.toSize(),
            alpha = 1f - alpha,
        )
    }
}
