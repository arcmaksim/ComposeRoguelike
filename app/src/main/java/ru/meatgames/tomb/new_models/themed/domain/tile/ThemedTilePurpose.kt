package ru.meatgames.tomb.new_models.themed.domain.tile

import ru.meatgames.tomb.new_models.themed.data.ThemedTilePurposeDto

enum class ThemedTilePurpose {
    Wall,
    WallDamaged,
    WallCracked,
    FloorVariant1,
    FloorVariant2,
    FloorVariant3,
    FloorVariant4,
    StairsUp,
    StairsDown,
    WallCrackedVertical,
    WallCrackedHorizontal,
    Empty,
}

fun ThemedTilePurposeDto?.toEntity(): ThemedTilePurpose = when (this) {
    ThemedTilePurposeDto.FloorVariant1 -> ThemedTilePurpose.FloorVariant1
    ThemedTilePurposeDto.FloorVariant2 -> ThemedTilePurpose.FloorVariant2
    ThemedTilePurposeDto.FloorVariant3 -> ThemedTilePurpose.FloorVariant3
    ThemedTilePurposeDto.FloorVariant4 -> ThemedTilePurpose.FloorVariant4
    ThemedTilePurposeDto.WallSingleDamaged -> ThemedTilePurpose.WallDamaged
    ThemedTilePurposeDto.WallSingleCracked -> ThemedTilePurpose.WallCracked
    ThemedTilePurposeDto.StairsUp -> ThemedTilePurpose.StairsUp
    ThemedTilePurposeDto.StairsDown -> ThemedTilePurpose.StairsDown
    ThemedTilePurposeDto.WallCrackedVertical -> ThemedTilePurpose.WallCrackedVertical
    ThemedTilePurposeDto.WallCrackedHorizontal -> ThemedTilePurpose.WallCrackedHorizontal
    ThemedTilePurposeDto.Wall0, ThemedTilePurposeDto.Wall1,
    ThemedTilePurposeDto.Wall2, ThemedTilePurposeDto.Wall3,
    ThemedTilePurposeDto.Wall4, ThemedTilePurposeDto.Wall5,
    ThemedTilePurposeDto.Wall6, ThemedTilePurposeDto.Wall7,
    ThemedTilePurposeDto.Wall8, ThemedTilePurposeDto.Wall9,
    ThemedTilePurposeDto.Wall10, ThemedTilePurposeDto.Wall11,
    ThemedTilePurposeDto.Wall12, ThemedTilePurposeDto.Wall13,
    ThemedTilePurposeDto.Wall14, ThemedTilePurposeDto.Wall15,
    ThemedTilePurposeDto.WallSingle, ThemedTilePurposeDto.Wall -> ThemedTilePurpose.Wall
    null -> ThemedTilePurpose.Empty
}
