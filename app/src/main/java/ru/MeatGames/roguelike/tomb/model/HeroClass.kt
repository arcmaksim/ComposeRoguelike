package ru.MeatGames.roguelike.tomb.model

import ru.MeatGames.roguelike.tomb.Global
import java.util.*

class HeroClass {

    // System vars
    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var mx: Int = 0
    var my: Int = 0
    lateinit var equipmentList: Array<Item?>
    var mIsFacingLeft: Boolean = true
    var mIsResting = false
        private set

    // Stats vars
    var regen: Int = 0
    var cregen: Int = 0
    var init = 10
    var mInventory: LinkedList<Item>? = null

    init {
        init()
    }

    fun init() {
        mInventory = null
        mInventory = LinkedList<Item>()
        equipmentList = arrayOfNulls<Item>(3)
        Global.stats[0].value = 10
        Global.stats[1].value = 10
        Global.stats[2].value = 10
        Global.stats[3].value = 10
        Global.stats[4].value = 10
        Global.stats[5].value = 18
        Global.stats[6].value = 18
        Global.stats[7].value = 10
        Global.stats[8].value = 10
        Global.stats[9].value = 10
        Global.stats[10].value = 10
        Global.stats[11].value = 2
        Global.stats[12].value = 1
        Global.stats[13].value = 3
        Global.stats[14].value = 10
        Global.stats[16].value = 10
        Global.stats[18].value = 1000
        Global.stats[19].value = 10
        Global.stats[20].value = 0
        Global.stats[21].value = 32
        Global.stats[22].value = 1
        Global.stats[25].value = 10
        Global.stats[27].value = 1000
        Global.stats[28].value = 10
        Global.stats[29].value = 1
        Global.stats[31].value = 1
        regen = 16
        cregen = regen
        addItem(Global.game.createItem(1))
        addItem(Global.game.createItem(4))
        addItem(Global.game.createItem(7))
        addItem(Global.game.createItem(10))
        addItem(Global.game.createItem(10))
        addItem(Global.game.createItem(10))
        for (i in 0..29) {
            addItem(Global.game.createItem(i % 11))
        }
        preequipItem(mInventory!![0])
        preequipItem(mInventory!![1])
        preequipItem(mInventory!![2])
    }

    fun getStat(id: Int) =
            Global.stats[id].value

    fun modifyStat(id: Int, value: Int, m: Int) {
        Global.stats[id].value = Global.stats[id].value + m * value
        if (Global.stats[id].mIsMaximum && Global.stats[id].value > Global.stats[id + 1].value)
            Global.stats[id].value = Global.stats[id + 1].value
        if (id == 20) isLevelUp()
    }

    fun isLevelUp() {
        if (getStat(20) >= getStat(21)) {
            modifyStat(20, getStat(21), -1)
            modifyStat(21, getStat(21), 1)
            modifyStat(31, 1, 1)
            Global.mapview.addLine("Уровень повышен!")
            val u = Global.game.mRandom.nextInt(3) + 2
            modifyStat(6, u, 1)
            modifyStat(5, u, 1)
            Global.mapview.addLine("Здоровье увеличено")
            if (getStat(31) % 4 == 0) {
                modifyStat(12, 1, 1)
                Global.mapview.addLine("Минимальный урон увеличен")
            }
            if (getStat(31) % 5 == 0) {
                regen--
                if (regen < cregen) cregen = regen
                Global.mapview.addLine("Скорость регенерации увеличена")
            }
            if (getStat(31) % 2 == 0) {
                modifyStat(13, 1, 1)
                Global.mapview.addLine("Максиммальный урон увеличен")
            }
            if (getStat(31) % 3 == 0) {
                modifyStat(19, 1, 1)
                Global.mapview.addLine("Защита увеличена")
            }
        }
    }

    fun addItem(item: Item) =
            mInventory!!.add(item)

    fun isEquipped(item: Item) =
            equipmentList[item.mType - 1] == item

    fun dropItem(item: Item) {
        if (!item.isConsumable && isEquipped(item)) {
            takeOffItem(item)
        }
        mInventory!!.remove(item)
        Global.map!![mx][my].addItem(item)
        Global.mapview.addLine(item.mTitle + " выброшен" + item.mTitleEnding)
        Global.game.skipTurn()
    }

    fun deleteItem(item: Item) =
            mInventory!!.remove(item)

    fun preequipItem(item: Item) {
        when (item.mType) {
            1 -> {
                equipmentList[0] = item
                modifyStat(11, item.mValue1, 1)
                modifyStat(12, item.mValue2, 1)
                modifyStat(13, item.mValue3, 1)
            }
            2 -> {
                equipmentList[1] = item
                modifyStat(19, item.mValue1, 1)
                modifyStat(22, item.mValue2, 1)
            }
            3 -> {
                equipmentList[2] = item
                modifyStat(19, item.mValue1, 1)
                modifyStat(22, item.mValue2, 1)
            }
        }
    }

    fun equipItem(item: Item) {
        when (item.mType) {
            1 -> {
                equipmentList[0] = item
                if (item.mProperty && equipmentList[1] != null) {
                    takeOffItem(1)
                }
                modifyStat(11, item.mValue1, 1)
                modifyStat(12, item.mValue2, 1)
                modifyStat(13, item.mValue3, 1)
            }
            2 -> {
                if (equipmentList[0] != null && equipmentList[0]!!.mProperty) {
                    takeOffItem(0)
                }
                equipmentList[1] = item
                modifyStat(19, item.mValue1, 1)
                modifyStat(22, item.mValue2, 1)
            }
            3 -> {
                equipmentList[2] = item
                modifyStat(19, item.mValue1, 1)
                modifyStat(22, item.mValue2, 1)
            }
        }
        Global.mapview.addLine(item.mTitle + " надет" + item.mTitleEnding)
        Global.game.skipTurn()
    }

    fun takeOffItem(item: Item) {
        when (item.mType) {
            1 -> {
                modifyStat(11, item.mValue1, -1)
                modifyStat(12, item.mValue2, -1)
                modifyStat(13, item.mValue3, -1)
            }
            2 -> {
                modifyStat(19, item.mValue1, -1)
                modifyStat(22, item.mValue2, -1)
            }
            3 -> {
                modifyStat(19, item.mValue1, -1)
                modifyStat(22, item.mValue2, -1)
            }
        }
        Global.hero!!.equipmentList[item.mType - 1] = null
        Global.mapview.addLine(item.mTitle + " снят" + item.mTitleEnding)
        Global.game.skipTurn()
    }

    fun takeOffItem(i: Int) =
            takeOffItem(equipmentList[i]!!)

    @JvmOverloads
    fun startResting(loudBroadcast: Boolean = true) {
        mIsResting = true
        if (loudBroadcast) {
            Global.mapview.addLine("Отдых начат")
        }
    }

    @JvmOverloads
    fun interruptResting(loudBroadcast: Boolean = true) {
        if (mIsResting) {
            mIsResting = false
            if (loudBroadcast) {
                Global.mapview.addLine("Отдых прерван!")
            }
        }
    }

    @JvmOverloads
    fun finishResting(loudBroadcast: Boolean = true) {
        if (mIsResting) {
            mIsResting = false
            if (loudBroadcast) {
                Global.mapview.addLine("Отдых завершен!")
            }
        }
    }

    fun isFullyHealed() =
            getStat(5) == getStat(6)

    // used for proper handling all ongoing hero actions
    // for example when changing screens
    fun interruptAllActions() {
        interruptResting(false)
    }

}