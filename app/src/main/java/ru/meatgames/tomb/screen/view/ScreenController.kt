package ru.meatgames.tomb.screen.view

import android.view.View
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.GameState
import ru.meatgames.tomb.InventoryFilterType
import ru.meatgames.tomb.MainActivity
import ru.meatgames.tomb.screen.Screens

class ScreenController(
        private val activity: MainActivity
) {

    var mGameScreen: GameScreen = GameScreen(activity)
    var mMainMenuScreen: MainMenu = MainMenu(activity)

    private lateinit var lastScreen: Screens


    fun initGameScreen(
            camX: Int,
            camY: Int
    ) {
        mGameScreen.camx = camX
        mGameScreen.camy = camY
    }

    fun changeScreen(
            screen: Screens
    ) {
        val view: View
        when (screen) {
            Screens.GAME_SCREEN -> {
                GameController.setState(GameState.MainGame)
                view = mGameScreen
                view.updateMapBuffer()
            }
            Screens.INVENTORY_SCREEN -> {
                GameController.setState(GameState.Inventory)
                lastScreen = Screens.INVENTORY_SCREEN
                view = InventoryScreen(activity, null)
            }
            Screens.CHARACTER_SCREEN -> {
                GameController.setState(GameState.Stats)
                view = CharacterScreen(activity)
            }
            Screens.MAP_SCREEN -> {
                GameController.setState(GameState.Map)
                view = MapScreen(activity)
            }
            Screens.GEAR_SCREEN -> {
                GameController.setState(GameState.Gear)
                lastScreen = Screens.GEAR_SCREEN
                view = GearScreen(activity)
            }
            Screens.DETAILED_ITEM_SCREEN -> {
                GameController.setState(GameState.ItemDetails)
                view = DetailedItemScreen(activity, GameController.selectedItem)
            }
            Screens.DEATH_SCREEN -> {
                GameController.setState(GameState.Death)
                view = DeathScreen(activity)
            }
            Screens.MAIN_MENU -> {
                GameController.setState(GameState.MainMenu)
                view = mMainMenuScreen
            }
        }

        // TODO: temporal solution
        //GameController.mHero.interruptAllActions()
        activity.setContentView(view)
        view.requestFocus()
    }

    fun changeScreen2(
            state: GameState
    ) {
        when (state) {
            else -> GameController.setState(state)
        }
        // TODO: temporal solution
        //GameController.mHero.interruptAllActions()
    }

    fun changeToLastScreen() = changeScreen(lastScreen)

    fun showInventoryWithFilters(
            filter: InventoryFilterType
    ) {
        val inventoryScreen = InventoryScreen(activity, filter)
        activity.setContentView(inventoryScreen)
        inventoryScreen.requestFocus()
    }

}