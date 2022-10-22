package ru.meatgames.tomb.new_models.tile.domain

data class ThemedTile(
    val theme: ThemedTileset,
    val purposeDefinition: ThemedTilePurposeDefinition,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
)
