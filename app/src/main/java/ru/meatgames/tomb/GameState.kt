package ru.meatgames.tomb

enum class GameState(val id: String) {
    MainMenu("mainMenu"),
    MainGame("mainGame"),
    Inventory("inventory"),
    Gear("gear"),
    ItemDetails("itemDetails"),
    Stats("stats"),
    Map("map"),
    Death("death"),
    Stub("stub")
}