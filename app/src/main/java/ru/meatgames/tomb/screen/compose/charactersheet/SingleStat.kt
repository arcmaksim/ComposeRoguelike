package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.model.AssetsLoader
import ru.meatgames.tomb.render.Illustration
import ru.meatgames.tomb.screen.compose.LocalIllustrationAssets

@Preview(widthDp = 240)
@Composable
private fun SingleStatPreview() {
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalIllustrationAssets provides AssetsLoader(context).illustrationAssets,
    ) {
        SingleStat(
            title = "Defense",
            value = "3",
            illustration = Illustration.Shield,
        )
    }
}

@Composable
fun SingleStat(
    title: String,
    illustration: Illustration,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val shape = RoundedCornerShape(STAT_CORNER_RADIUS_DP.dp)
    var width = remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier.then(
            Modifier
                .background(
                    color = Color.DarkGray,
                    shape = shape,
                )
                .clip(shape)
                .onSizeChanged {
                    width.intValue = it.width
                }
                .padding(horizontal = STAT_HORIZONTAL_PADDING_DP.dp)
                .height(STAT_HEIGHT_DP.dp),
        ),
    ) {
        Illustration(
            modifier = Modifier.offset {
                IntOffset(
                    x = width.intValue - STAT_ILLUSTRATION_SIZE_DP.dp.roundToPx() + STAT_ILLUSTRATION_HORIZONTAL_OFFSET_DP.dp.roundToPx(),
                    y = (-STAT_ILLUSTRATION_VERTICAL_OFFSET_DP).dp.roundToPx(),
                )
            },
            illustration = illustration,
            sizeDp = STAT_ILLUSTRATION_SIZE_DP,
            alpha = .3f,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = STAT_TOP_PADDING_DP.dp)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.offset(y = 2.dp),
                text = title,
                style = h3TextStyle,
            )
            Text(
                modifier = Modifier.offset(y = (-2).dp),
                text = value,
                style = h1TextStyle,
            )
        }
    }
}
