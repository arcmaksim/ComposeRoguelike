package ru.meatgames.tomb

import androidx.compose.ui.unit.IntOffset

enum class Direction {
    Left,
    Right,
    Top,
    Bottom,
}

val Direction.resolvedOffsets: Pair<Int, Int>
    get() = when (this) {
        Direction.Left -> -1 to 0
        Direction.Right -> 1 to 0
        Direction.Top -> 0 to -1
        Direction.Bottom -> 0 to 1
    }

fun Direction.toIntOffset(
    dimension: Int,
): IntOffset {
    val (x, y) = resolvedOffsets
    return IntOffset(x * dimension, y * dimension)
}
