package ru.MeatGames.roguelike.tomb.new_models.provider

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import ru.MeatGames.roguelike.tomb.new_models.repo.*

class GameDataProvider {

	lateinit var stats: StatRepo
	lateinit var armor: ArmorRepo
	lateinit var weapons: WeaponRepo
	lateinit var shields: ShieldRepo
	lateinit var items: ItemRepo


	fun init(context: Context) {
		stats = LoganSquare.parse(context.assets.open("stats.json"), StatRepo::class.java)
		armor = LoganSquare.parse(context.assets.open("armor.json"), ArmorRepo::class.java)
		weapons = LoganSquare.parse(context.assets.open("weapons.json"), WeaponRepo::class.java)
		shields = LoganSquare.parse(context.assets.open("shields.json"), ShieldRepo::class.java)
		items = LoganSquare.parse(context.assets.open("items.json"), ItemRepo::class.java)
	}

}