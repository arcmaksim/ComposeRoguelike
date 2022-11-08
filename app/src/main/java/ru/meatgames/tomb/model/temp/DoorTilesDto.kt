package ru.meatgames.tomb.model.temp

import kotlinx.serialization.Serializable
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile

@Serializable
class DoorTilesDto(
    val tiles: List<DoorsTileDto>,
    val themes: List<DoorsThemeDto>,
) {

    @Serializable
    class DoorsTileDto(
        val tile: ObjectRenderTile,
        val horizontalOffset: Int,
    )

    @Serializable
    class DoorsThemeDto(
        val name: String,
        val verticalOffset: Int,
    )

}
