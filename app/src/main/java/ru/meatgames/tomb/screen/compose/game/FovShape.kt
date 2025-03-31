package ru.meatgames.tomb.screen.compose.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity

@Composable
internal fun FovShape() {
    val density = LocalDensity.current
    val tileSize = LocalTileSize.current
    val tileDimension = tileSize.width

    BoxWithConstraints {
        val width = with(density) { this@BoxWithConstraints.maxWidth.toPx() }
        val height = with(density) { this@BoxWithConstraints.maxHeight.toPx() }
        val circleOffset = tileDimension * .5f
        val circleCenter = Offset(width / 2f, height / 2f)

        val screenPath = remember {
            Path().apply {
                addRect(Rect(Offset.Zero, Size(width, height)))
            }
        }

        // Create a path for the circle we want to exclude
        val innerPartPath = remember {
            val asd = Path().apply {
                moveTo(
                    circleCenter.x - circleOffset - tileDimension,
                    circleCenter.y - circleOffset - tileDimension,
                )
                lineTo(
                    circleCenter.x + circleOffset + tileDimension,
                    circleCenter.y - circleOffset - tileDimension,
                )
                lineTo(
                    circleCenter.x + circleOffset + tileDimension,
                    circleCenter.y + circleOffset + tileDimension,
                )
                lineTo(
                    circleCenter.x - circleOffset - tileDimension,
                    circleCenter.y + circleOffset + tileDimension,
                )
            }
            Path().apply {
                op(screenPath, asd, PathOperation.Difference)
            }
        }

        val outerPartPath = remember {
            val asd = Path().apply {
                moveTo(circleCenter.x - circleOffset - tileDimension, circleCenter.y - circleOffset - tileDimension * 2)
                lineTo(circleCenter.x + circleOffset + tileDimension, circleCenter.y - circleOffset - tileDimension * 2)

                lineTo(circleCenter.x + circleOffset + tileDimension, circleCenter.y - circleOffset - tileDimension)
                lineTo(circleCenter.x + circleOffset + tileDimension * 2, circleCenter.y - circleOffset - tileDimension)

                lineTo(circleCenter.x + circleOffset + tileDimension * 2, circleCenter.y + circleOffset + tileDimension)
                lineTo(circleCenter.x + circleOffset + tileDimension, circleCenter.y + circleOffset + tileDimension)

                lineTo(circleCenter.x + circleOffset + tileDimension, circleCenter.y + circleOffset + tileDimension * 2)
                lineTo(circleCenter.x - circleOffset - tileDimension, circleCenter.y + circleOffset + tileDimension * 2)

                lineTo(circleCenter.x - circleOffset - tileDimension, circleCenter.y + circleOffset + tileDimension)
                lineTo(circleCenter.x - circleOffset - tileDimension * 2, circleCenter.y + circleOffset + tileDimension)

                lineTo(circleCenter.x - circleOffset - tileDimension * 2, circleCenter.y - circleOffset - tileDimension)
                lineTo(circleCenter.x - circleOffset - tileDimension, circleCenter.y - circleOffset - tileDimension)

                lineTo(circleCenter.x - circleOffset - tileDimension, circleCenter.y - circleOffset - tileDimension * 2)
            }
            Path().apply {
                op(screenPath, asd, PathOperation.Difference)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            clipPath(innerPartPath) {
                drawRect(Color(0x60212121))
            }

            clipPath(outerPartPath) {
                drawRect(Color(0x90212121))
            }
        }
    }
}
