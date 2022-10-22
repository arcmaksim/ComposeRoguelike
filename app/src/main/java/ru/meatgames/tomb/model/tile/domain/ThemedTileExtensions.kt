package ru.meatgames.tomb.model.tile.domain

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.model.room.domain.ThemedRoomSymbolMapping

fun ThemedTile.getOffset(
    tileSize: Int = 24,
): IntOffset = when (purposeDefinition) {
    is ThemedTilePurposeDefinition.Standard -> IntOffset(
        x = purposeDefinition.horizontalTileOffset * tileSize,
        y = theme.verticalTileOffset * tileSize,
    )
    is ThemedTilePurposeDefinition.General -> {
        val (offsetX, offsetY) = purposeDefinition.getOffsets()
        IntOffset(
            x = offsetX * tileSize,
            y = offsetY * tileSize,
        )
    }
}

private fun ThemedTilePurposeDefinition.General.getOffsets(): Pair<Int, Int> = when (purpose) {
    GeneralTilePurpose.ClosedDoor -> 3 to 0
    GeneralTilePurpose.OpenDoor -> 1 to 1
}

fun ThemedTile.getSize(
    tileSize: Int = 24,
): IntSize = IntSize(
    width = tileSize,
    height = tileSize,
)

fun Char.toThemedTile(
    tileset: ThemedTileset,
    tiles: List<ThemedTilePurposeDefinition>,
    symbolMapping: List<ThemedRoomSymbolMapping>,
): ThemedTile {
    val mapping = symbolMapping.first { it.symbol == this }
    val purposeDefinition = tiles.first {
        (it as ThemedTilePurposeDefinition.Standard).purpose == mapping.purpose
    } as ThemedTilePurposeDefinition.Standard
    return purposeDefinition.toThemedTile(tileset)
}

fun ThemedTilePurposeDefinition.Standard.toThemedTile(
    themedTileset: ThemedTileset,
): ThemedTile {
    val (isPassable, isTransparent, isUsable) = when (purpose) {
        ThemedTilePurpose.Empty, ThemedTilePurpose.FloorVariant1,
        ThemedTilePurpose.FloorVariant2, ThemedTilePurpose.FloorVariant3,
        ThemedTilePurpose.FloorVariant4 -> Triple(true, true, false)
        ThemedTilePurpose.StairsDown -> Triple(false, true, true)
        ThemedTilePurpose.StairsUp -> Triple(false, false, true)
        ThemedTilePurpose.Wall, ThemedTilePurpose.WallCracked,
        ThemedTilePurpose.WallDamaged, ThemedTilePurpose.WallCrackedVertical,
        ThemedTilePurpose.WallCrackedHorizontal -> Triple(false, false, false)
    }

    return ThemedTile(
        theme = themedTileset,
        purposeDefinition = this,
        isPassable = isPassable,
        isUsable = isUsable,
        isTransparent = isTransparent,
    )
}
