package ru.meatgames.tomb.model.tile.domain

import ru.meatgames.tomb.model.tile.data.ThemedTilePurposeDefinitionDto


sealed class ThemedTilePurposeDefinition {

    // TODO: rename
    data class Standard(
        val purpose: ThemedTilePurpose,
        val horizontalTileOffset: Int,
    ) : ThemedTilePurposeDefinition()

    data class General(
        val purpose: GeneralTilePurpose,
    ) : ThemedTilePurposeDefinition()

}

val ThemedTilePurposeDefinition.isEmpty: Boolean
    get() = when (this) {
        is ThemedTilePurposeDefinition.Standard -> purpose == ThemedTilePurpose.Empty
        is ThemedTilePurposeDefinition.General -> false
    }

fun ThemedTilePurposeDefinitionDto.toEntity(): ThemedTilePurposeDefinition =
    ThemedTilePurposeDefinition.Standard(
        purpose = purpose.toEntity(),
        horizontalTileOffset = horizontalTileOffset,
    )

fun ThemedTilePurposeDefinition.General.toThemedTile(
    themedTileset: ThemedTileset,
): ThemedTile {
    val (isPassable, isTransparent, isUsable) = when (purpose) {
        GeneralTilePurpose.OpenDoor -> Triple(true, true, false)
        GeneralTilePurpose.ClosedDoor -> Triple(false, true, true)
    }

    return ThemedTile(
        theme = themedTileset,
        purposeDefinition = this,
        isPassable = isPassable,
        isUsable = isUsable,
        isTransparent = isTransparent,
    )
}