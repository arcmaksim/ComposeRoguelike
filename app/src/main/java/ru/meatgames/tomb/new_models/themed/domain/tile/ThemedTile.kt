package ru.meatgames.tomb.new_models.themed.domain.tile

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

data class ThemedTile(
    val theme: ThemedTileset,
    val purposeDefinition: ThemedTilePurposeDefinition,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
)

fun ThemedTile.getOffset(
    tileSize: Int = 24,
): IntOffset = IntOffset(
    x = purposeDefinition.horizontalTileOffset * tileSize,
    y = theme.verticalTileOffset * tileSize,
)

fun ThemedTile.getSize(
    tileSize: Int = 24,
): IntSize = IntSize(
    width = tileSize,
    height = tileSize,
)