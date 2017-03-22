package ru.MeatGames.roguelike.tomb

import ru.MeatGames.roguelike.tomb.model.HeroClass
import ru.MeatGames.roguelike.tomb.model.MapClass
import ru.MeatGames.roguelike.tomb.util.MapHelper
import ru.MeatGames.roguelike.tomb.util.array2d

object GameController {

    private lateinit var mMainActivity: Game
    private lateinit var mState: GameState

    private lateinit var mHero: HeroClass

    private var mMapWidth: Int = 96
    private var mMapHeight: Int = 96
    private lateinit var mMap: Array<Array<MapClass>>

    @JvmStatic
    fun init(mainActivity: Game) {
        mMainActivity = mainActivity
        mState = GameState.MAIN_MENU
    }

    @JvmStatic
    fun startNewGame() {
        mHero = HeroClass()
        generateNewMap()
    }

    @JvmStatic
    fun generateNewMap() {
        MapHelper.mapWidth = mMapWidth
        MapHelper.mapHeight = mMapHeight
        mMap = array2d(mMapWidth, mMapHeight) { MapClass() }
    }

    @JvmStatic
    fun endGame() {

    }

}