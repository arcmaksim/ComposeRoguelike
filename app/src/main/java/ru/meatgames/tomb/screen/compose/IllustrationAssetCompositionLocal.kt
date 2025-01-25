package ru.meatgames.tomb.screen.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import ru.meatgames.tomb.model.IllustrationAssets

val LocalIllustrationAssets = compositionLocalOf { IllustrationAssets(ImageBitmap(0, 0)) }
