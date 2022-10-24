package ru.meatgames.tomb.model.tile.domain

import ru.meatgames.tomb.model.tile.data.TilePurposeDefinitionDto


sealed class TilePurposeDefinition {

    // TODO: rename
    data class Standard(
        val purpose: TilePurpose,
        val horizontalTileOffset: Int,
    ) : TilePurposeDefinition()

    data class General(
        val purpose: GeneralTilePurpose,
    ) : TilePurposeDefinition()

}

val TilePurposeDefinition.isEmpty: Boolean
    get() = when (this) {
        is TilePurposeDefinition.Standard -> purpose == TilePurpose.Empty
        is TilePurposeDefinition.General -> false
    }

fun TilePurposeDefinitionDto.toEntity(): TilePurposeDefinition =
    TilePurposeDefinition.Standard(
        purpose = purpose.toEntity(),
        horizontalTileOffset = horizontalTileOffset,
    )

fun TilePurposeDefinition.General.toTile(
    tileset: Tileset,
): Tile {
    val (isPassable, isTransparent, isUsable) = when (purpose) {
        GeneralTilePurpose.OpenDoor -> Triple(true, true, false)
        GeneralTilePurpose.ClosedDoor -> Triple(false, false, true)
    }

    return Tile(
        theme = tileset,
        purposeDefinition = this,
        isPassable = isPassable,
        isUsable = isUsable,
        isTransparent = isTransparent,
    )
}
