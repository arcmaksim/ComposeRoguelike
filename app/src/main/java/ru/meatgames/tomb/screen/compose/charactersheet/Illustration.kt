package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.model.AssetsLoader
import ru.meatgames.tomb.render.Illustration
import ru.meatgames.tomb.screen.compose.LocalIllustrationAssets

@Preview(showBackground = true)
@Composable
private fun IllustrationPreview() {
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalIllustrationAssets provides AssetsLoader(context).illustrationAssets,
    ) {
        Illustration(
            illustration = Illustration.Heart,
            sizeDp = STAT_ILLUSTRATION_SIZE_DP,
        )
    }
}

@Composable
fun Illustration(
    illustration: Illustration,
    sizeDp: Int,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    val density = LocalDensity.current
    val illustrationAssets = LocalIllustrationAssets.current

    val renderData = remember(illustration) {
        illustrationAssets.getRenderData(illustration)
    }
    val size = remember {
        val sizePx = with(density) { STAT_ILLUSTRATION_SIZE_DP.dp.roundToPx() }
        IntSize(sizePx, sizePx)
    }

    Box(
        modifier = modifier.then(
            Modifier
                .size(sizeDp.dp)
                .drawWithContent {
                    drawImage(
                        image = renderData.asset,
                        srcOffset = renderData.offset,
                        srcSize = renderData.size,
                        dstOffset = IntOffset.Zero,
                        dstSize = size,
                        alpha = alpha,
                        filterQuality = FilterQuality.None,
                    )
                },
        ),
        content = { Unit },
    )
}
