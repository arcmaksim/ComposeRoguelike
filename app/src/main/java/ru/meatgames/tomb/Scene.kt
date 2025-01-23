package ru.meatgames.tomb

enum class Scene(val id: String) {
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
