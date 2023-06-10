package ru.meatgames.tomb

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.domain.Offset
import androidx.compose.ui.geometry.Offset as ComposeOffset

enum class Direction {
    Left,
    Right,
    Up,
    Down,
}

val Direction.resolvedOffset: Offset
    get() = when (this) {
        Direction.Left -> -1 to 0
        Direction.Right -> 1 to 0
        Direction.Up -> 0 to -1
        Direction.Down -> 0 to 1
    }

fun Direction.toIntOffset(
    dimension: Int,
): IntOffset {
    val (x, y) = resolvedOffset
    return IntOffset(x * dimension, y * dimension)
}

context(Density)
internal fun ComposeOffset.toDirection(
    size: Dp,
): Direction = when {
    x > y && x.toDp() > size - y.toDp() -> Direction.Right
    x > y -> Direction.Up
    x < y && y.toDp() > size - x.toDp() -> Direction.Down
    else -> Direction.Left
}
