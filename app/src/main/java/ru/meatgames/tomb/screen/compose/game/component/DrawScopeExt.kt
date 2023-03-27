package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.model.temp.ASSETS_TILE_SIZE
import ru.meatgames.tomb.model.temp.getOriginalTileSinglePixelOffset
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.render.RenderData

context(DrawScope)
fun RenderData.drawImage(
    dstSize: IntSize,
    dstOffset: IntOffset,
) {
    drawImage(
        image = asset,
        srcOffset = offset,
        srcSize = ASSETS_TILE_SIZE,
        dstOffset = dstOffset,
        dstSize = dstSize,
        filterQuality = FilterQuality.None,
    )
}

fun DrawScope.drawCharacter(
    tileDimension: Int,
    shadowRenderData: RenderData,
    frameIndex: Int,
    characterRenderData: AnimationRenderData,
    dstSize: IntSize,
    dstOffset: IntOffset,
) {
    val originalTileSinglePixelOffset = tileDimension.getOriginalTileSinglePixelOffset()
    val verticalOffset = originalTileSinglePixelOffset * 3
    
    shadowRenderData.drawImage(
        dstSize = dstSize,
        dstOffset = dstOffset + IntOffset(
            x = characterRenderData.shadowHorizontalOffset * originalTileSinglePixelOffset,
            y = originalTileSinglePixelOffset - verticalOffset,
        ),
    )
    drawImage(
        image = characterRenderData.asset,
        srcOffset = characterRenderData.offsets[frameIndex],
        srcSize = ASSETS_TILE_SIZE,
        dstOffset = dstOffset + IntOffset(
            x = 0,
            y = -verticalOffset,
        ),
        dstSize = dstSize,
        filterQuality = FilterQuality.None,
    )
}
