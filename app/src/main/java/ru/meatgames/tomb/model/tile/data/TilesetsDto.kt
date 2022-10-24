package ru.meatgames.tomb.model.tile.data

@kotlinx.serialization.Serializable
class TilesetsDto(
    val themes: List<TilesetThemeDto>,
    val tilePurpose: List<TilePurposeDefinitionDto>,
)
