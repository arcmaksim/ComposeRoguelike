package ru.meatgames.tomb.model.theme

import kotlinx.serialization.Serializable
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile

@Serializable
class FloorTilesDto(
    val tiles: List<FloorTileDto>,
    val themes: List<FloorThemeDto>,
) {

    @Serializable
    class FloorTileDto(
        val tile: FloorRenderTile,
        val horizontalOffset: Int,
    )

    @Serializable
    class FloorThemeDto(
        val name: String,
        val verticalOffset: Int,
    )

}
