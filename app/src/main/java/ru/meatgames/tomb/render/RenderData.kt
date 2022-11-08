package ru.meatgames.tomb.render

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset

data class RenderData(
    val asset: ImageBitmap,
    val srcOffset: IntOffset,
)
