package ru.meatgames.tomb.new_models.provider

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import ru.meatgames.tomb.new_models.repo.*

class GameDataProvider {

	lateinit var stats: StatRepo
	lateinit var armor: ArmorRepo
	lateinit var weapons: WeaponRepo
	lateinit var shields: ShieldRepo
	lateinit var items: ItemRepo


	fun init(context: Context) {
		stats = LoganSquare.parse(context.assets.open("raw/stats.json"), StatRepo::class.java)
		armor = LoganSquare.parse(context.assets.open("raw/armor.json"), ArmorRepo::class.java)
		weapons = LoganSquare.parse(context.assets.open("raw/weapons.json"), WeaponRepo::class.java)
		shields = LoganSquare.parse(context.assets.open("raw/shields.json"), ShieldRepo::class.java)
		items = LoganSquare.parse(context.assets.open("raw/items.json"), ItemRepo::class.java)
	}

}