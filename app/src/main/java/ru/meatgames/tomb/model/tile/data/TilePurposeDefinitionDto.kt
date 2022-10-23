package ru.meatgames.tomb.model.tile.data

@kotlinx.serialization.Serializable
class TilePurposeDefinitionDto(
    val purpose: TilePurposeDto,
    val horizontalTileOffset: Int,
)
