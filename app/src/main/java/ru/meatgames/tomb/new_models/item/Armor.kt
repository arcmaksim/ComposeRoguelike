package ru.meatgames.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Armor {

	companion object {
		const val LEATHER_ARMOR: Int = 1000
		const val CHAINMAIL: Int = 1001
		const val PLATE_ARMOR: Int = 1002
	}

	@JsonField(name = ["id"]) var id: Int = 0
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["ending"]) var ending: String = ""
	@JsonField(name = ["statModifiers"]) var statModifiers: List<StatModifier>? = null

}