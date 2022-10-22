package ru.meatgames.tomb.new_models.tile.data

@kotlinx.serialization.Serializable
enum class ThemedTilePurposeDto {
    // all tiles from tileset
    WallSingle,
    WallSingleDamaged,
    WallSingleCracked,
    FloorVariant1,
    FloorVariant2,
    FloorVariant3,
    FloorVariant4,
    StairsUp,
    StairsDown,
    Wall0,
    Wall1,
    Wall2,
    Wall3,
    Wall4,
    Wall5,
    Wall6,
    Wall7,
    Wall8,
    Wall9,
    Wall10,
    Wall11,
    Wall12,
    Wall13,
    Wall14,
    Wall15,
    WallCrackedVertical,
    WallCrackedHorizontal,
    // specific tile for room definition
    Wall,
    Empty,
}
