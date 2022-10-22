package ru.meatgames.tomb.new_models.tile.data

@kotlinx.serialization.Serializable
class ThemedTilesetsDto(
    val themes: List<TilesetThemeDto>,
    val themedTiles: List<ThemedTilePurposeDefinitionDto>,
)
