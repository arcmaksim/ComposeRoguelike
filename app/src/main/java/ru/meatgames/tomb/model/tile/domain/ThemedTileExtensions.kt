package ru.meatgames.tomb.model.tile.domain

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.model.room.domain.RoomSymbolMapping

fun Tile.getOffset(
    tileSize: Int = 24,
): IntOffset = when (purposeDefinition) {
    is TilePurposeDefinition.Standard -> IntOffset(
        x = purposeDefinition.horizontalTileOffset * tileSize,
        y = theme.verticalTileOffset * tileSize,
    )
    is TilePurposeDefinition.General -> {
        val (offsetX, offsetY) = purposeDefinition.getOffsets()
        IntOffset(
            x = offsetX * tileSize,
            y = offsetY * tileSize,
        )
    }
}

private fun TilePurposeDefinition.General.getOffsets(): Pair<Int, Int> = when (purpose) {
    GeneralTilePurpose.ClosedDoor -> 3 to 0
    GeneralTilePurpose.OpenDoor -> 1 to 1
}

fun Tile.getSize(
    tileSize: Int = 24,
): IntSize = IntSize(
    width = tileSize,
    height = tileSize,
)

fun Char.toTile(
    tileset: Tileset,
    tiles: List<TilePurposeDefinition>,
    symbolMapping: List<RoomSymbolMapping>,
): Tile {
    val mapping = symbolMapping.first { it.symbol == this }
    val purposeDefinition = tiles.first {
        (it as TilePurposeDefinition.Standard).purpose == mapping.purpose
    } as TilePurposeDefinition.Standard
    return purposeDefinition.toTile(tileset)
}

fun TilePurposeDefinition.Standard.toTile(
    tileset: Tileset,
): Tile {
    val (isPassable, isTransparent, isUsable) = when (purpose) {
        TilePurpose.Empty, TilePurpose.FloorVariant1,
        TilePurpose.FloorVariant2, TilePurpose.FloorVariant3,
        TilePurpose.FloorVariant4 -> Triple(true, true, false)
        TilePurpose.StairsDown -> Triple(false, true, true)
        TilePurpose.StairsUp -> Triple(false, false, true)
        TilePurpose.Wall, TilePurpose.WallCracked,
        TilePurpose.WallDamaged, TilePurpose.WallCrackedVertical,
        TilePurpose.WallCrackedHorizontal -> Triple(false, false, false)
    }

    return Tile(
        theme = tileset,
        purposeDefinition = this,
        isPassable = isPassable,
        isUsable = isUsable,
        isTransparent = isTransparent,
    )
}
