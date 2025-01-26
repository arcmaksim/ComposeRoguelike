package ru.meatgames.tomb.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.render.Illustration
import ru.meatgames.tomb.screen.compose.LocalIllustrationAssets
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

private const val BUTTON_SIZE_DP = 64
private const val PADDING_DP = 12

@Composable
fun IllustrationButton(
    illustration: Illustration,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = { Unit },
) {
    val density = LocalDensity.current
    val illustrationAssets = LocalIllustrationAssets.current

    val shape = RoundedCornerShape(16.dp)
    val colorMatrix = remember(enabled) {
        ColorMatrix().apply {
            if (!enabled) setToSaturation(0f)
        }
    }
    val renderData = remember(illustration) {
        illustrationAssets.getRenderData(illustration)
    }
    val size = remember {
        val sizePx = with(density) { BUTTON_SIZE_DP.dp.roundToPx() }
        val paddingPx = with(density) { PADDING_DP.dp.roundToPx() }
        IntSize(sizePx - paddingPx * 2, sizePx - paddingPx * 2)
    }
    val offset = remember {
        val paddingPx = with(density) { PADDING_DP.dp.roundToPx() }
        IntOffset(paddingPx, paddingPx)
    }

    Box(
        modifier = modifier.then(
            Modifier
                .size(BUTTON_SIZE_DP.dp)
                .background(
                    color = Color.DarkGray,
                    shape = shape,
                )
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    enabled = enabled,
                    onClick = onClick,
                    role = Role.Button,
                )
                .drawWithContent {
                    drawImage(
                        colorFilter = ColorFilter.colorMatrix(colorMatrix),
                        image = renderData.asset,
                        srcOffset = renderData.offset,
                        srcSize = renderData.size,
                        dstOffset = offset,
                        dstSize = size,
                        filterQuality = FilterQuality.None,
                    )
                },
        ),
        contentAlignment = Alignment.Center,
        content = { Unit },
    )
}
