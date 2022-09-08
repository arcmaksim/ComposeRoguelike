package ru.meatgames.tomb.new_models.themed.domain.tile

import ru.meatgames.tomb.new_models.themed.data.ThemedTilePurposeDto

enum class ThemedTilePurpose {
    WallFlat,
    WallFlatCracked,
    WallFlatDamaged,
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
    Empty,
}

fun ThemedTilePurposeDto?.toEntity(): ThemedTilePurpose = when (this) {
    ThemedTilePurposeDto.FloorVariant1 -> ThemedTilePurpose.FloorVariant1
    ThemedTilePurposeDto.FloorVariant2 -> ThemedTilePurpose.FloorVariant2
    ThemedTilePurposeDto.FloorVariant3 -> ThemedTilePurpose.FloorVariant3
    ThemedTilePurposeDto.FloorVariant4 -> ThemedTilePurpose.FloorVariant4
    ThemedTilePurposeDto.WallFlat -> ThemedTilePurpose.WallFlat
    ThemedTilePurposeDto.WallFlatCracked -> ThemedTilePurpose.WallFlatCracked
    ThemedTilePurposeDto.WallFlatDamaged -> ThemedTilePurpose.WallFlatDamaged
    ThemedTilePurposeDto.StairsUp -> ThemedTilePurpose.StairsUp
    ThemedTilePurposeDto.StairsDown -> ThemedTilePurpose.StairsDown
    ThemedTilePurposeDto.WallCrackedVertical -> ThemedTilePurpose.WallCrackedVertical
    ThemedTilePurposeDto.WallCrackedHorizontal -> ThemedTilePurpose.WallCrackedHorizontal
    ThemedTilePurposeDto.Wall0 -> ThemedTilePurpose.Wall0
    ThemedTilePurposeDto.Wall1 -> ThemedTilePurpose.Wall1
    ThemedTilePurposeDto.Wall2 -> ThemedTilePurpose.Wall2
    ThemedTilePurposeDto.Wall3 -> ThemedTilePurpose.Wall3
    ThemedTilePurposeDto.Wall4 -> ThemedTilePurpose.Wall4
    ThemedTilePurposeDto.Wall5 -> ThemedTilePurpose.Wall5
    ThemedTilePurposeDto.Wall6 -> ThemedTilePurpose.Wall6
    ThemedTilePurposeDto.Wall7 -> ThemedTilePurpose.Wall7
    ThemedTilePurposeDto.Wall8 -> ThemedTilePurpose.Wall8
    ThemedTilePurposeDto.Wall9 -> ThemedTilePurpose.Wall9
    ThemedTilePurposeDto.Wall10 -> ThemedTilePurpose.Wall10
    ThemedTilePurposeDto.Wall11 -> ThemedTilePurpose.Wall11
    ThemedTilePurposeDto.Wall12 -> ThemedTilePurpose.Wall12
    ThemedTilePurposeDto.Wall13 -> ThemedTilePurpose.Wall13
    ThemedTilePurposeDto.Wall14 -> ThemedTilePurpose.Wall14
    ThemedTilePurposeDto.Wall15 -> ThemedTilePurpose.Wall15
    null -> ThemedTilePurpose.Empty
}
