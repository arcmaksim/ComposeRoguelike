package ru.meatgames.tomb.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun Canvas.fillFrame(
    width: Int,
    height: Int,
    backgroundColor: Paint,
) {
    drawRect(
        0F,
        0F,
        width.toFloat(),
        height.toFloat(),
        backgroundColor,
    )
}

inline fun <reified INNER> array2d(
    sizeOuter: Int,
    sizeInner: Int,
    noinline innerInit: (Int) -> INNER,
): Array<Array<INNER>> = Array(sizeOuter) { Array(sizeInner, innerInit) }

inline fun Int.formatNumber(): String = if (this > 0) "+ $this" else "$this"

inline fun Rect.asIntOffset() = IntOffset(left, top)

inline fun Rect.asIntSize() = IntSize(width(), height())