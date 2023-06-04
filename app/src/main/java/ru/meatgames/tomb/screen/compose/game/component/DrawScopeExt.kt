package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.model.theme.ASSETS_TILE_SIZE
import ru.meatgames.tomb.model.theme.getOriginalTileSinglePixelOffset
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.render.RenderData
import ru.meatgames.tomb.screen.compose.game.animation.EnemyAnimationState

context(DrawScope)
fun RenderData.drawImage(
    dstSize: IntSize,
    dstOffset: IntOffset,
    alpha: Float = 1f,
) {
    drawImage(
        alpha = alpha,
        image = asset,
        srcOffset = offset,
        srcSize = size,
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
    alpha: Float,
    iconState: EnemyAnimationState.Icon?,
) {
    val originalTileSinglePixelOffset = tileDimension.getOriginalTileSinglePixelOffset()
    val verticalOffset = originalTileSinglePixelOffset * 3
    
    if (FeatureToggles.getToggleValue(FeatureToggle.ShowEnemiesHealthBar) && characterRenderData.healthRatio != 0f) {
        val topLeftOffset = dstOffset.toOffset() + Offset(x = 0f, y = -verticalOffset * 2f)
        val innerOffset = topLeftOffset.copy(
            x = topLeftOffset.x + originalTileSinglePixelOffset.toFloat(),
            y = topLeftOffset.y + originalTileSinglePixelOffset.toFloat(),
        )
        drawRect(
            alpha = alpha,
            color = Color.White,
            topLeft = topLeftOffset,
            size = Size(width = dstSize.width.toFloat(), height = verticalOffset.toFloat()),
        )
        drawRect(
            alpha = alpha,
            color = Color.Red,
            topLeft = innerOffset,
            size = Size(
                width = (dstSize.width - originalTileSinglePixelOffset * 2) * characterRenderData.healthRatio,
                height = originalTileSinglePixelOffset.toFloat(),
            ),
        )
    }
    
    shadowRenderData.drawImage(
        dstSize = dstSize,
        dstOffset = dstOffset + IntOffset(
            x = characterRenderData.shadowHorizontalOffset * originalTileSinglePixelOffset,
            y = originalTileSinglePixelOffset - verticalOffset,
        ),
        alpha = alpha,
    )
    drawImage(
        alpha = alpha,
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
    
    iconState?.let {
        val iconDimension = tileDimension / 2
        it.renderData.drawImage(
            dstOffset = dstOffset.copy(
                x = dstOffset.x + (tileDimension - iconDimension) / 2,
                y = dstOffset.y + (tileDimension - iconDimension) / 2 - verticalOffset,
            ),
            dstSize = IntSize(iconDimension, iconDimension),
            alpha = it.iconAlpha,
        )
    }
}
