package ru.meatgames.tomb.model.temp

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile

data class FloorThemes(
    val atlas: ImageBitmap,
    val themes: List<Theme>,
) {

    data class Theme(
        val title: String,
        val map: Map<FloorRenderTile, IntOffset>,
    )

}
