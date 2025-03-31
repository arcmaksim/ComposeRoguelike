package ru.meatgames.tomb

enum class Scene(val id: String) {
    Root("root"),

    MainMenuRoot("mainMenuRoot"),
    MainMenu("mainMenu"),

    MainGameRoot("mainGameRoot"),
    MainGame("mainGame"),
    WinScreen("winScreen"),
    DeathScreen("deathScreen"),

    GameScreenDialog("GameScreenDialog"),
    ContainerDialog("ContainerDialog/{itemContainerId}"),
    FeatureToggles("FeatureToggles"),
    
    Inventory("inventory"),
    Stats("stats"),
}
