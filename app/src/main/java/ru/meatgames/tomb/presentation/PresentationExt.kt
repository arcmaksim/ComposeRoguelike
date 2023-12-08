package ru.meatgames.tomb.presentation

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun IntOffset.multiply(
    size: IntSize,
): IntOffset = IntOffset(
    x = x * -size.width,
    y = y * -size.height,
)

fun IntOffset.multiply(
    value: Int,
): IntOffset = multiply(value, value)

fun IntOffset.multiply(
    xMultiplier: Int,
    yMultiplier: Int,
): IntOffset = IntOffset(
    x = x * xMultiplier,
    y = y * yMultiplier,
)
