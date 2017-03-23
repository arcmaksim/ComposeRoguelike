package ru.MeatGames.roguelike.tomb.screen

import android.view.View
import ru.MeatGames.roguelike.tomb.*

class ScreenController {

    private var mMainActivity: MainActivity

    lateinit var mapview: GameScreen
    lateinit var mmview: MainMenu

    private lateinit var lastScreen: Screens

    constructor(mainActivity: MainActivity) {
        mMainActivity = mainActivity
    }

    init {

    }

    fun changeScreen(screen: Screens) {
        val view: View
        when (screen) {
            Screens.GAME_SCREEN -> {
                GameController.setState(GameState.MAIN_GAME)
                view = Assets.mapview
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
                view = Assets.mmview
            }
        }

        // TODO: temporal solution
        //GameController.mHero.interruptAllActions()
        mMainActivity.setContentView(view)
        view.requestFocus()
    }

    fun changeToLastScreen() {
        changeScreen(lastScreen)
    }

    fun showInventoryWithFilters(filter: InventoryFilterType) {
        val inventoryScreen = InventoryScreen(mMainActivity, filter)
        mMainActivity.setContentView(inventoryScreen)
        inventoryScreen.requestFocus()
    }

}