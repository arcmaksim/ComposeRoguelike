package ru.meatgames.tomb.new_models.themed.data.tile

@kotlinx.serialization.Serializable
class ThemedTilesetsDto(
    val themes: List<TilesetThemeDto>,
    val themedTiles: List<ThemedTilePurposeDefinitionDto>,
)
