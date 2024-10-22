package ru.meatgames.tomb

enum class GameState(val id: String) {
    MainMenu("mainMenu"),
    MainGame("mainGame"),
    WinScreen("winScreen"),
    Stub("stub"),
    
    GameScreenDialog("GameScreenDialog"),
    ContainerDialog("ContainerDialog/{itemContainerId}"),
    FeatureToggles("FeatureToggles"),
    
    Inventory("inventory"),
    Stats("stats"),
}
