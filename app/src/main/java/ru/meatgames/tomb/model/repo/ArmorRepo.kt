package ru.meatgames.tomb.model.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.item.Armor

@JsonObject
class ArmorRepo {

	@JsonField(name = ["armor"]) var armor: List<Armor> = arrayListOf()


	fun getArmor(itemId: Int): Armor = armor.first { it.id == itemId }

}