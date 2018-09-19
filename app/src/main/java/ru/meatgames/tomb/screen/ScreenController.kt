package ru.meatgames.tomb.screen

import android.view.View
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.GameState
import ru.meatgames.tomb.InventoryFilterType
import ru.meatgames.tomb.MainActivity

class ScreenController {

    private var mMainActivity: MainActivity

    lateinit var mGameScreen: GameScreen
    lateinit var mMainMenuScreen: MainMenu

    private lateinit var lastScreen: Screens

    constructor(mainActivity: MainActivity) {
        mMainActivity = mainActivity

        init()
    }

    private fun init() {
        mMainMenuScreen = MainMenu(mMainActivity)
        mGameScreen = GameScreen(mMainActivity)
    }

    fun initGameScreen(camX: Int, camY: Int) {
        mGameScreen.camx = camX
        mGameScreen.camy = camY
    }

    fun changeScreen(screen: Screens) {
        val view: View
        when (screen) {
            Screens.GAME_SCREEN -> {
                GameController.setState(GameState.MAIN_GAME)
                view = mGameScreen
                view.updateMapBuffer()
            }
            Screens.INVENTORY_SCREEN -> {
                GameController.setState(GameState.INVENTORY_SCREEN)
                lastScreen = Screens.INVENTORY_SCREEN
                view = InventoryScreen(mMainActivity, null)
            }
            Screens.CHARACTER_SCREEN -> {
                GameController.setState(GameState.STATS_SCREEN)
                view = CharacterScreen(mMainActivity)
            }
            Screens.MAP_SCREEN -> {
                GameController.setState(GameState.MAP_SCREEN)
                view = MapScreen(mMainActivity)
            }
            Screens.GEAR_SCREEN -> {
                GameController.setState(GameState.GEAR_SCREEN)
                lastScreen = Screens.GEAR_SCREEN
                view = GearScreen(mMainActivity)
            }
            Screens.DETAILED_ITEM_SCREEN -> {
                GameController.setState(GameState.DETAILED_ITEM_SCREEN)
                view = DetailedItemScreen(mMainActivity, GameController.selectedItem)
            }
            Screens.DEATH_SCREEN -> {
                GameController.setState(GameState.DEATH_SCREEN)
                view = DeathScreen(mMainActivity)
            }
            Screens.MAIN_MENU -> {
                GameController.setState(GameState.MAIN_MENU)
                view = mMainMenuScreen
            }
        }

        // TODO: temporal solution
        //GameController.mHero.interruptAllActions()
        mMainActivity.setContentView(view)
        view.requestFocus()
    }

    fun changeToLastScreen() = changeScreen(lastScreen)

    fun showInventoryWithFilters(filter: InventoryFilterType) {
        val inventoryScreen = InventoryScreen(mMainActivity, filter)
        mMainActivity.setContentView(inventoryScreen)
        inventoryScreen.requestFocus()
    }

}