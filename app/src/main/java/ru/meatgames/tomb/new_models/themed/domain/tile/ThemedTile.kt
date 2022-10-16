package ru.meatgames.tomb.new_models.themed.domain.tile

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.new_models.tile.GeneralTilePurpose

data class ThemedTile(
    val theme: ThemedTileset,
    val purposeDefinition: ThemedTilePurposeDefinition,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
)

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
