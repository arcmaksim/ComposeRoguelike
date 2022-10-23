package ru.meatgames.tomb.model.tile.domain

import ru.meatgames.tomb.model.tile.data.TilePurposeDto

enum class TilePurpose {
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

fun TilePurposeDto.toEntity(): TilePurpose = when (this) {
    TilePurposeDto.FloorVariant1 -> TilePurpose.FloorVariant1
    TilePurposeDto.FloorVariant2 -> TilePurpose.FloorVariant2
    TilePurposeDto.FloorVariant3 -> TilePurpose.FloorVariant3
    TilePurposeDto.FloorVariant4 -> TilePurpose.FloorVariant4
    TilePurposeDto.WallSingleDamaged -> TilePurpose.WallDamaged
    TilePurposeDto.WallSingleCracked -> TilePurpose.WallCracked
    TilePurposeDto.StairsUp -> TilePurpose.StairsUp
    TilePurposeDto.StairsDown -> TilePurpose.StairsDown
    TilePurposeDto.WallCrackedVertical -> TilePurpose.WallCrackedVertical
    TilePurposeDto.WallCrackedHorizontal -> TilePurpose.WallCrackedHorizontal
    TilePurposeDto.Wall0, TilePurposeDto.Wall1,
    TilePurposeDto.Wall2, TilePurposeDto.Wall3,
    TilePurposeDto.Wall4, TilePurposeDto.Wall5,
    TilePurposeDto.Wall6, TilePurposeDto.Wall7,
    TilePurposeDto.Wall8, TilePurposeDto.Wall9,
    TilePurposeDto.Wall10, TilePurposeDto.Wall11,
    TilePurposeDto.Wall12, TilePurposeDto.Wall13,
    TilePurposeDto.Wall14, TilePurposeDto.Wall15,
    TilePurposeDto.WallSingle, TilePurposeDto.Wall -> TilePurpose.Wall
    TilePurposeDto.Empty -> TilePurpose.Empty
}
