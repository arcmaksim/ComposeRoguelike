package ru.meatgames.tomb.model.theme

data class CurrentTheme(
    val wallsTheme: WallsThemes.Theme,
    val floorTheme: FloorThemes.Theme,
    val stairsTheme: StairsThemes.Theme,
    val doorsTheme: DoorsThemes.Theme,
)
