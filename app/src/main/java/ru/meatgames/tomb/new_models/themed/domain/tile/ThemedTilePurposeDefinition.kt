package ru.meatgames.tomb.new_models.themed.domain.tile

import ru.meatgames.tomb.new_models.themed.data.tile.ThemedTilePurposeDefinitionDto

data class ThemedTilePurposeDefinition(
    val purpose: ThemedTilePurpose,
    val horizontalTileOffset: Int,
)

fun ThemedTilePurposeDefinitionDto.toEntity(): ThemedTilePurposeDefinition = ThemedTilePurposeDefinition(
    purpose = purpose.toEntity(),
    horizontalTileOffset = horizontalTileOffset,
)
