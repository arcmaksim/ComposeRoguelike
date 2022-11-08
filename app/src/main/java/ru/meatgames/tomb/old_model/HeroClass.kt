package ru.meatgames.tomb.old_model

import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.model.item.InventoryItem
import java.util.*

class HeroClass {

    // System vars
    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var mx: Int = 0
    var my: Int = 0
    lateinit var equipmentList: Array<InventoryItem?>
    var mIsFacingLeft: Boolean = true
    var mIsResting = false
        private set

    // Stats vars
    var regen: Int = 0
    var cregen: Int = 0
    var init = 10
    var mInventory: MutableList<InventoryItem> = mutableListOf()

    init {
        init()
    }

    fun init() {
        mInventory.clear()
        equipmentList = arrayOfNulls(3)
        Assets.stats[0].value = 10
        Assets.stats[1].value = 10
        Assets.stats[2].value = 10
        Assets.stats[3].value = 10
        Assets.stats[4].value = 10
        Assets.stats[5].value = 18
        Assets.stats[6].value = 18
        Assets.stats[7].value = 10
        Assets.stats[8].value = 10
        Assets.stats[9].value = 10
        Assets.stats[10].value = 10
        Assets.stats[11].value = 2
        Assets.stats[12].value = 1
        Assets.stats[13].value = 3
        Assets.stats[14].value = 10
        Assets.stats[16].value = 10
        Assets.stats[18].value = 1000
        Assets.stats[19].value = 10
        Assets.stats[20].value = 0
        Assets.stats[21].value = 32
        Assets.stats[22].value = 1
        Assets.stats[25].value = 10
        Assets.stats[27].value = 1000
        Assets.stats[28].value = 10
        Assets.stats[29].value = 1
        Assets.stats[31].value = 1
        regen = 16
        cregen = regen
        addItem(GameController.createItem())
        addItem(GameController.createItem())
        addItem(GameController.createItem())
        addItem(GameController.createItem())
    }

    fun getStat(id: Int) =
            Assets.stats[id].value

    fun modifyStat(id: Int, value: Int, m: Int) {
        Assets.stats[id].value = Assets.stats[id].value + m * value
        if (Assets.stats[id].mIsMaximum && Assets.stats[id].value > Assets.stats[id + 1].value)
            Assets.stats[id].value = Assets.stats[id + 1].value
        if (id == 20) isLevelUp()
    }

    fun isLevelUp() {
        if (getStat(20) >= getStat(21)) {
            modifyStat(20, getStat(21), -1)
            modifyStat(21, getStat(21), 1)
            modifyStat(31, 1, 1)
            GameController.updateLog("Уровень повышен!")
            val healthIncreaseAmount = Random().nextInt(3) + 2
            modifyStat(6, healthIncreaseAmount, 1)
            modifyStat(5, healthIncreaseAmount, 1)
            GameController.updateLog("Здоровье увеличено")
            if (getStat(31) % 4 == 0) {
                modifyStat(12, 1, 1)
                GameController.updateLog("Минимальный урон увеличен")
            }
            if (getStat(31) % 5 == 0) {
                regen--
                if (regen < cregen) cregen = regen
                GameController.updateLog("Скорость регенерации увеличена")
            }
            if (getStat(31) % 2 == 0) {
                modifyStat(13, 1, 1)
                GameController.updateLog("Максиммальный урон увеличен")
            }
            if (getStat(31) % 3 == 0) {
                modifyStat(19, 1, 1)
                GameController.updateLog("Защита увеличена")
            }
        }
    }

    fun addItem(item: InventoryItem) = mInventory.add(item)

    fun isEquipped(item: InventoryItem) = equipmentList.contains(item)

    @JvmOverloads
    fun startResting(loudBroadcast: Boolean = true) {
        mIsResting = true
        if (loudBroadcast) {
            GameController.updateLog("Отдых начат")
        }
    }

    @JvmOverloads
    fun interruptResting(loudBroadcast: Boolean = true) {
        if (mIsResting) {
            mIsResting = false
            if (loudBroadcast) {
                GameController.updateLog("Отдых прерван!")
            }
        }
    }

    @JvmOverloads
    fun finishResting(loudBroadcast: Boolean = true) {
        if (mIsResting) {
            mIsResting = false
            if (loudBroadcast) {
                GameController.updateLog("Отдых завершен!")
            }
        }
    }

    fun isFullyHealed() =
            getStat(5) == getStat(6)

    // used for proper handling all ongoing hero actions
    // for example when changing screens
    fun interruptAllActions() = interruptResting(false)

}
