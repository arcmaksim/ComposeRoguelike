package ru.meatgames.tomb.model.tile.data

@kotlinx.serialization.Serializable
class ThemedTilePurposeDefinitionDto(
    val purpose: ThemedTilePurposeDto,
    val horizontalTileOffset: Int,
)
