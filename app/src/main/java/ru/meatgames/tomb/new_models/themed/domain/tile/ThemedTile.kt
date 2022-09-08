package ru.meatgames.tomb.new_models.themed.domain.tile

import ru.meatgames.tomb.new_models.themed.data.tile.ThemedTileDto

data class ThemedTile(
    val purpose: ThemedTilePurpose,
    val horizontalTileOffset: Int,
)

fun ThemedTileDto.toEntity(): ThemedTile = ThemedTile(
    purpose = purpose.toEntity(),
    horizontalTileOffset = horizontalTileOffset,
)
