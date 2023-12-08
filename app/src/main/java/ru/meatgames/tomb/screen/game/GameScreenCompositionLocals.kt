package ru.meatgames.tomb.screen.game

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.design.backgroundColor

val LocalTileSize = compositionLocalOf { IntSize(0, 0) }

val LocalHorizontalOffset = compositionLocalOf { IntOffset(0, 0) }

val LocalBackgroundColor = compositionLocalOf { backgroundColor }
