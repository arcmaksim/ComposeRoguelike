package ru.meatgames.tomb.model.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.item.Weapon

@JsonObject
class WeaponRepo {

	@JsonField(name = ["weapons"]) var weapons: List<Weapon> = emptyList()


	fun getWeapon(itemId: Int): Weapon = weapons.first { it.id == itemId }

}
