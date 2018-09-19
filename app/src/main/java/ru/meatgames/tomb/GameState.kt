package ru.meatgames.tomb

enum class GameState(val state: Int) {
    MAIN_MENU(0),
    MAIN_GAME(1),
    INVENTORY_SCREEN(2),
    GEAR_SCREEN(3),
    DETAILED_ITEM_SCREEN(4),
    STATS_SCREEN(5),
    MAP_SCREEN(6),
    DEATH_SCREEN(7)
}