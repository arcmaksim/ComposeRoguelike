package ru.meatgames.tomb.model.provider

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import ru.meatgames.tomb.model.repo.*

object GameDataProvider {

    lateinit var stats: StatRepo
    lateinit var armor: ArmorRepo
    lateinit var weapons: WeaponRepo
    lateinit var shields: ShieldRepo
    lateinit var consumables: ConsumableRepo

    fun init(
        context: Context,
    ) {
        stats = LoganSquare.parse(
            context.assets.open("data/stats.json"),
            StatRepo::class.java,
        )
        /*armor = LoganSquare.parse(
            context.assets.open("data/armor.json"),
            ArmorRepo::class.java,
        )
        weapons = LoganSquare.parse(
            context.assets.open("data/weapons.json"),
            WeaponRepo::class.java,
        )
        shields = LoganSquare.parse(
            context.assets.open("data/shields.json"),
            ShieldRepo::class.java,
        )
        consumables = LoganSquare.parse(
            context.assets.open("data/consumables.json"),
            ConsumableRepo::class.java,
        )*/
    }

}
