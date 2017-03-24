package ru.MeatGames.roguelike.tomb

import android.content.Context
import android.graphics.Bitmap
import android.os.Vibrator
import ru.MeatGames.roguelike.tomb.model.HeroClass
import ru.MeatGames.roguelike.tomb.model.Item
import ru.MeatGames.roguelike.tomb.model.MapClass
import ru.MeatGames.roguelike.tomb.screen.ScreenController
import ru.MeatGames.roguelike.tomb.screen.Screens
import ru.MeatGames.roguelike.tomb.util.MapHelper
import ru.MeatGames.roguelike.tomb.util.array2d
import java.util.*

object GameController {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mState: GameState
    private lateinit var mVibrator: Vibrator

    private lateinit var mScreenController: ScreenController
    private lateinit var mMapController: MapController

    lateinit var mHero: HeroClass

    var mIsPlayerTurn = true
    var mIsPlayerMoved = false
    var mAcceptPlayerInput: Boolean = false

    lateinit var zone: Array<Array<Int>>
    private val zoneDefaultValue = 99

    // TODO: temporal solution
    lateinit var selectedItem: Item
    lateinit var lastAttack: Bitmap

    private var mMainGameThread: Thread? = null
    private var mMainGameLoop: MainGameLoop? = null

    var mDrawInputAreas = false

    var curLvls = 0
    val maxLvl = 3
    val maxMobs = 6

    @JvmStatic
    fun init(mainActivity: MainActivity) {
        mMainActivity = mainActivity
        mVibrator = mMainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        init()
    }

    private fun init() {
        mScreenController = ScreenController(mMainActivity)
        mMapController = MapController()
        zone = array2d(11, 11) { 0 }
    }

    @JvmStatic
    fun start() {
        changeScreen(Screens.MAIN_MENU)
        curLvls = 0
        newGameLoop()
    }

    @JvmStatic
    fun setState(state: GameState) {
        mState = state
    }

    @JvmStatic
    fun vibrate(vibratePeriod: Long = 30L) = mVibrator.vibrate(vibratePeriod)

    fun updateLOS(x: Int = mHero.mx, y: Int = mHero.my) = mScreenController.mGameScreen.calculateLineOfSight(x, y)

    fun updateLog(message: String) = mScreenController.mGameScreen.addLine(message)

    fun updateMapBuffer() = mScreenController.mGameScreen.updateMapBuffer()

    @JvmStatic
    fun showExitDialog() {
        mScreenController.mGameScreen.mDrawExitDialog = true
    }

    @JvmStatic
    fun startNewGame() {
        mHero = HeroClass()
        generateNewMap()
        changeScreen(Screens.GAME_SCREEN)
        mMainGameThread!!.start()
    }

    @JvmStatic
    fun initNewGame(mapX: Int, mapY: Int) {
        mScreenController.initGameScreen(mapX - 2, mapY - 2)
        mHero.mx = mapX + 2
        mHero.my = mapY + 2
    }

    @JvmStatic
    fun getMap(): Array<Array<MapClass>> = mMapController.getMap()

    @JvmStatic
    fun generateNewMap() = mMapController.generateNewMap()

    fun changeScreen(screen: Screens) = mScreenController.changeScreen(screen)

    fun changeToLastScreen() = mScreenController.changeToLastScreen()

    fun showInventoryWithFilters(filter: InventoryFilterType) = mScreenController.showInventoryWithFilters(filter)

    @JvmStatic
    fun exitGame() = mMainActivity.exitGame()

    @JvmStatic
    fun createItem(): Item {
        val item = Item(Random().nextInt(13))
        /*item.id = mRandom.nextInt(13);
        item.title = Assets.itemDB[item.id].title;
        item.mTitleEnding = Assets.itemDB[item.id].mTitleEnding;
        item.mType = Assets.itemDB[item.id].mType;
        item.mValue1 = Assets.itemDB[item.id].mValue1;
        item.mValue2 = Assets.itemDB[item.id].mValue2;
        item.mValue3 = Assets.itemDB[item.id].mValue3;
        item.mProperty = Assets.itemDB[item.id].mProperty;*/
        return item
    }

    @JvmStatic
    fun createItem(t: Int): Item {
        val item = Item(t)
        /*item.id = t;
        item.title = Assets.itemDB[item.id].title;
        item.mTitleEnding = Assets.itemDB[item.id].mTitleEnding;
        item.mType = Assets.itemDB[item.id].mType;
        item.mValue1 = Assets.itemDB[item.id].mValue1;
        item.mValue2 = Assets.itemDB[item.id].mValue2;
        item.mValue3 = Assets.itemDB[item.id].mValue3;
        item.mProperty = Assets.itemDB[item.id].mProperty;*/
        return item
    }

    fun createItem(x4: Int, y4: Int) {
        val item = createItem()
        mMapController.getMap()[x4][y4].addItem(item)
    }

    fun skipTurn() {
        mAcceptPlayerInput = false
        mIsPlayerTurn = false
        updateZone()
    }

    fun spread(i1: Int, j1: Int, c: Int) {
        for (i in i1 - 1..i1 + 2 - 1)
            for (j in j1 - 1..j1 + 2 - 1)
                if (zone[i][j] == zoneDefaultValue
                        && mMapController.getMap()[mScreenController.mGameScreen.camx - 1 + i][mScreenController.mGameScreen.camy - 1 + j].mIsPassable
                        && !mMapController.getMap()[mScreenController.mGameScreen.camx - 1 + i][mScreenController.mGameScreen.camy - 1 + j].hasMob())
                    zone[i][j] = c
    }

    fun clearZone() {
        for (i in 0..10)
            for (j in 0..10)
                zone[i][j] = zoneDefaultValue
        zone[5][5] = 0
    }

    fun updateZone() {
        clearZone()
        val xl: Int
        val xr: Int
        val yl: Int
        val yr: Int
        xl = if (mScreenController.mGameScreen.camx - 1 < 1) 1 else mScreenController.mGameScreen.camx - 1
        yl = if (mScreenController.mGameScreen.camy - 1 < 1) 1 else mScreenController.mGameScreen.camy - 1
        xr = if (mScreenController.mGameScreen.camx + 10 > MapHelper.mMapWidth - 2)
            MapHelper.mMapHeight - 2
        else
            mScreenController.mGameScreen.camx + 10
        yr = if (mScreenController.mGameScreen.camy + 10 > MapHelper.mMapWidth - 2)
            MapHelper.mMapHeight - 2
        else
            mScreenController.mGameScreen.camy + 10
        for (c in 0..4)
            for (i in xl..xr - 1)
                for (j in yl..yr - 1)
                    if (zone[i - xl][j - yl] == c)
                        spread(i - xl, j - yl, c + 1)
    }

    fun isCollision(mx: Int, my: Int) {
        val mapCell = MapHelper.getMapTile(mx, my)
        if (mapCell != null) {
            if (mapCell.mIsUsable) {
                when (MapHelper.getObjectId(mx, my)) {
                    2 -> {
                        MapHelper.changeObject(mx, my, 3)
                        mScreenController.mGameScreen.addLine(mMainActivity.getString(R.string.door_opened_message))
                        mIsPlayerMoved = false
                    }
                    4 -> {
                        MapHelper.changeObject(mx, my, 5)
                        mScreenController.mGameScreen.mDrawLog = false
                        mScreenController.mGameScreen.initProgressBar(4, 159)
                        mIsPlayerMoved = false
                    }
                    7 -> {
                        mScreenController.mGameScreen.mDrawLog = false
                        mScreenController.mGameScreen.initProgressBar(7, 259)
                        mIsPlayerMoved = false
                    }
                }
                mIsPlayerTurn = false
                return
            }
            if (mIsPlayerTurn && mapCell.hasMob()) {
                attack(mapCell)
                return
            }
            if (mapCell.mIsPassable) {
                mIsPlayerTurn = false
                mIsPlayerMoved = true // ?
                if (mapCell.mObjectID == 15) {
                    mHero.modifyStat(5, Random().nextInt(3) + 1, -1)
                    mScreenController.mGameScreen.addLine(mMainActivity.getString(R.string.trap_message))
                    if (mHero.getStat(5) < 1) {
                        lastAttack = Bitmap.createScaledBitmap(Assets.objects[15].img, 72, 72, false)
                        changeScreen(Screens.DEATH_SCREEN)
                    }
                }
            } else {
                mScreenController.mGameScreen.addLine(mMainActivity.getString(R.string.path_is_blocked_message))
                vibrate()
                mIsPlayerMoved = false
            }
        }
    }

    fun attack(map: MapClass) {
        /*val random = Random()

        val att = random.nextInt(20) + 1 + Assets.hero!!.getStat(11)
        if (att >= map.mob.mob.mDefense) {
            var u = random.nextInt(Assets.hero!!.getStat(13) - Assets.hero!!.getStat(12) + 1) + Assets.hero!!.getStat(12) - map.mob.mob.mArmor
            if (u < 1) {
                u = 1
            }
            map.mob.mob.mHealth = map.mob.mob.mHealth - u
            Assets.mGameScreen.addLine(map.mob.mob.name + getString(R.string.is_receiving_damage_message))
            if (map.mob.mob.mHealth < 1) {
                Assets.mGameScreen.addLine(map.mob.mob.name + getString(R.string.is_dying_message))
                Assets.hero!!.modifyStat(20, map.mob.t, 1)
                if (map.mob.t == maxMobs - 1)
                    Assets.mGameScreen.mDrawWinScreen = true
                deleteMob(map)
                var x4: Int
                var y4: Int
                do {
                    x4 = random.nextInt(MapHelper.mMapWidth)
                    y4 = random.nextInt(MapHelper.mMapHeight)
                } while (!Assets.map!![x4][y4].mIsPassable || Assets.map!![x4][y4].mCurrentlyVisible || Assets.map!![x4][y4].hasMob())
                val en = random.nextInt(Assets.getGame().maxMobs - curLvls - 1) + curLvls
                if (en < 3 && random.nextInt(3) == 0) {
                    if (Assets.map!![x4 - 1][y4].mIsPassable && !Assets.map!![x4 - 1][y4].hasItem())
                        Assets.getGame().createMob(x4 - 1, y4, en)
                    if (Assets.map!![x4 + 1][y4].mIsPassable && !Assets.map!![x4 + 1][y4].hasItem())
                        Assets.getGame().createMob(x4 + 1, y4, en)
                }
                Assets.getGame().createMob(x4, y4, en)
            }
        } else {
            Assets.mGameScreen.addLine(getString(R.string.attack_missed_message))
        }
        mIsPlayerTurn = false*/
    }

    fun move(mx: Int, my: Int) {
        mHero.interruptResting()
        mScreenController.mGameScreen.mx = mx
        mScreenController.mGameScreen.my = my
        isCollision(mHero.mx + mx, mHero.my + my)
        if (mIsPlayerMoved) {
            mHero.mx = mHero.mx + mx
            mHero.my = mHero.my + my
            mScreenController.mGameScreen.camx = mScreenController.mGameScreen.camx + mx
            mScreenController.mGameScreen.camy = mScreenController.mGameScreen.camy + my
        }
        mScreenController.mGameScreen.calculateLineOfSight(mHero.mx, mHero.my)
        if (mx == 1) mHero.mIsFacingLeft = false
        if (mx == -1) mHero.mIsFacingLeft = true
        if ((mx != 0 || my != 0) && mMapController.getMap()[mHero.mx][mHero.my].hasItem()) {
            if (mMapController.getMap()[mHero.mx][mHero.my].mItems.size > 1) {
                mScreenController.mGameScreen.addLine(mMainActivity.getString(R.string.several_items_lying_on_the_ground_message))
            } else {
                mScreenController.mGameScreen.addLine(mMapController.getMap()[mHero.mx][mHero.my].mItems[0].mTitle
                        + mMainActivity.getString(R.string.lying_on_the_ground_message))
            }
        }
        updateZone()
        mScreenController.mGameScreen.updateMapBuffer()
    }

    fun deleteMob(map: MapClass) {
        /*val temp = map.mob
        if (map.mob === firstMob) {
            firstMob = firstMob.next
        } else {
            var cur: MobList
            cur = firstMob
            while (cur.next !== map.mob) {
                cur = cur.next
            }
            cur.next = map.mob.next
        }
        temp.map.mob = null
        temp.map = null*/
    }

    fun mobAttack(mob: MobList) {
        /*Assets.hero!!.interruptResting()

        val att = mRandom.nextInt(20) + 1 + mob.mob.mArmor
        if (att >= Assets.hero!!.getStat(19)) {
            var u = mob.mob.mDamage - Assets.hero!!.getStat(22)
            if (u < 1) {
                u = 1
            }
            Assets.hero!!.modifyStat(5, u, -1)
            Assets.mGameScreen.addLine(mob.mob.name + getString(R.string.is_dealing_damage_message))
        } else {
            Assets.mGameScreen.addLine(mob.mob.name + getString(R.string.is_missing_attack_message))
        }
        if (Assets.hero!!.getStat(5) < 1) {
            Assets.hero!!.modifyStat(5, Assets.hero!!.getStat(5), -1)
            lastAttack = Bitmap.createScaledBitmap(mob.getImg(0), 72, 72, false)
            runOnUiThread(Runnable { changeScreen(Screens.DEATH_SCREEN) })
        }*/
    }

    fun gameOver() {
        mMainGameThread?.let {
            mMainGameLoop?.terminate()
        }
        mMainGameThread = null
        changeScreen(Screens.MAIN_MENU)
    }

    fun newGameLoop() {
        if (mMainGameLoop == null) {
            mMainGameLoop = MainGameLoop()
            mMainGameThread = Thread(mMainGameLoop)
        }
        //(mMainGameThread as Thread).start()
    }

}