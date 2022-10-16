package ru.meatgames.tomb.new_models.themed.data.tile

import ru.meatgames.tomb.new_models.themed.data.ThemedTilePurposeDto

@kotlinx.serialization.Serializable
class ThemedTilePurposeDefinitionDto(
    val purpose: ThemedTilePurposeDto,
    val horizontalTileOffset: Int,
)