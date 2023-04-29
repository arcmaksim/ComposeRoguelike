package ru.meatgames.tomb.model.theme

import kotlinx.serialization.Serializable
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile

@Serializable
class WallTilesDto(
    val tiles: List<WallTileDto>,
    val themes: List<WallThemeDto>,
) {

    @Serializable
    class WallTileDto(
        val tile: ObjectRenderTile,
        val horizontalOffset: Int,
    )

    @Serializable
    class WallThemeDto(
        val name: String,
        val verticalOffset: Int,
    )

}
