package ru.meatgames.tomb

enum class GameState(val id: String) {
    MainMenu("mainMenu"),
    MainGame("mainGame"),
    WinScreen("winScreen"),
    Stub("stub"),
    
    GameScreenDialog("GameScreenDialog"),
    
    Inventory("inventory"),
    Gear("gear"),
    ItemDetails("itemDetails"),
    Stats("stats"),
    Map("map"),
    Death("death"),
}
