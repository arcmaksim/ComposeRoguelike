package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize

@Preview(widthDp = 436, heightDp = 436)
@Composable
private fun GameScreenMapPreview() {
    val context = LocalContext.current
    
    NewAssets.loadAssets(context)
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
            val column = index % tilesWidth - tilesPadding
            val row = index / tilesWidth - tilesPadding
            val dstOffset = offset + initialOffset + animatedOffset + IntOffset(
                column * tileDimension,
                row * tileDimension,
            )
            
            val tileScreenSpaceCoordinates = column to row
            
            if (renderTile is MapRenderTile.Content && renderTile.isVisible) {
                val alpha = revealedTilesAlpha.takeIf { tilesToReveal.contains(tileScreenSpaceCoordinates) }
                drawRevealedTile(
                    renderTile = renderTile,
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    alpha = alpha,
                    backgroundColor = backgroundColor,
                )
            }
            if (renderTile is MapRenderTile.Content && !renderTile.isVisible
                && tilesToFade.contains(tileScreenSpaceCoordinates)
            ) {
                drawRevealedTile(
                    renderTile = renderTile,
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    alpha = fadedTilesAlpha,
                    backgroundColor = backgroundColor,
                )
            }
        }
    }
}

private fun DrawScope.drawRevealedTile(
    renderTile: MapRenderTile.Content,
    dstOffset: IntOffset,
    tileSize: IntSize,
    backgroundColor: Color,
    alpha: Float?,
) {
    val filterQuality = FilterQuality.None
    
    drawImage(
        image = renderTile.floorData.asset,
        srcOffset = renderTile.floorData.srcOffset,
        srcSize = NewAssets.tileSize,
        dstOffset = dstOffset,
        dstSize = tileSize,
        filterQuality = filterQuality,
    )
    renderTile.objectData?.let { objectTile ->
        drawImage(
            image = objectTile.asset,
            srcOffset = objectTile.srcOffset,
            srcSize = NewAssets.tileSize,
            dstOffset = dstOffset,
            dstSize = tileSize,
            filterQuality = filterQuality,
        )
    }
    alpha?.let {
        drawRect(
            topLeft = dstOffset.toOffset(),
            color = backgroundColor,
            alpha = it,
            size = tileSize.toSize(),
        )
    }
}