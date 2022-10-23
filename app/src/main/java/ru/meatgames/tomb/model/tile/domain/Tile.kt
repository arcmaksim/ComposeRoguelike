package ru.meatgames.tomb.model.tile.domain

data class Tile(
    val theme: Tileset,
    val purposeDefinition: TilePurposeDefinition,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val isUsable: Boolean,
)
