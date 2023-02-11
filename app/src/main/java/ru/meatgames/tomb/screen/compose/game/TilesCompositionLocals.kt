package ru.meatgames.tomb.screen.compose.game

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

val LocalTileSize = compositionLocalOf { IntSize(0, 0) }

val LocalHorizontalOffset = compositionLocalOf { IntOffset(0, 0) }

val LocalBackgroundColor = compositionLocalOf { Color(0xFF212121) }
