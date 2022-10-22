package ru.meatgames.tomb.new_models.tile.data

import ru.meatgames.tomb.new_models.tile.data.ThemedTilePurposeDto

@kotlinx.serialization.Serializable
class ThemedTilePurposeDefinitionDto(
    val purpose: ThemedTilePurposeDto,
    val horizontalTileOffset: Int,
)
