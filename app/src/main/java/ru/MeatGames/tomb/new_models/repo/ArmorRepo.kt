package ru.meatgames.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.new_models.item.Armor

@JsonObject
class ArmorRepo {

	@JsonField(name = ["armor"]) var armor: List<Armor> = arrayListOf()


	fun getArmor(itemId: Int): Armor = armor.first { it.id == itemId }

}