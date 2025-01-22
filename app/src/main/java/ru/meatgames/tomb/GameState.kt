package ru.meatgames.tomb

enum class GameState(val id: String) {
    MainMenu("mainMenu"),
    MainGame("mainGame"),
    WinScreen("winScreen"),
    DeathScreen("deathScreen"),

    GameScreenDialog("GameScreenDialog"),
    ContainerDialog("ContainerDialog/{itemContainerId}"),
    FeatureToggles("FeatureToggles"),
    
    Inventory("inventory"),
    Stats("stats"),
}
