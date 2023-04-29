package ru.meatgames.tomb.model.theme

import kotlinx.serialization.Serializable
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile

@Serializable
class StairTilesDto(
    val tiles: List<StairsTileDto>,
    val themes: List<StairsThemeDto>,
) {

    @Serializable
    class StairsTileDto(
        val tile: ObjectRenderTile,
        val horizontalOffset: Int,
    )

    @Serializable
    class StairsThemeDto(
        val name: String,
        val verticalOffset: Int,
    )

}
