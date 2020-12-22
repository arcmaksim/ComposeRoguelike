package ru.meatgames.tomb

enum class GameState(val id: String) {
    MAIN_MENU("mainMenu"),
    MAIN_GAME("mainGame"),
    INVENTORY_SCREEN("inventory"),
    GEAR_SCREEN("gear"),
    DETAILED_ITEM_SCREEN("itemDetails"),
    STATS_SCREEN("stats"),
    MAP_SCREEN("map"),
    DEATH_SCREEN("death")
}