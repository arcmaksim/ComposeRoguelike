package ru.meatgames.tomb.model.theme

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile

data class WallsThemes(
    val atlas: ImageBitmap,
    val themes: List<Theme>,
) {

    data class Theme(
        val title: String,
        val map: Map<ObjectRenderTile, IntOffset>,
    )

}
