package ru.meatgames.tomb.model.tile.data

@kotlinx.serialization.Serializable
class ThemedTilesetsDto(
    val themes: List<TilesetThemeDto>,
    val themedTiles: List<ThemedTilePurposeDefinitionDto>,
)
