package ru.MeatGames.roguelike.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.MeatGames.roguelike.tomb.new_models.item.Weapon

@JsonObject
class WeaponRepo {

	@JsonField(name = ["weapons"]) var weapons: List<Weapon> = emptyList()


	fun getWeapon(itemId: Int): Weapon = weapons.first { it.id == itemId }

}