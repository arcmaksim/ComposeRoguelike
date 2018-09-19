package ru.meatgames.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Weapon {

	companion object {
		const val KNIFE: Int = 0
		const val SHORT_SWORD: Int = 1
		const val LONG_SWORD: Int = 2
		const val GREATSWORD: Int = 3
	}

	@JsonField(name = ["id"]) var id: Int = 0
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["ending"]) var ending: String = ""
	@JsonField(name = ["isTwoHanded"]) var twoHanded: Boolean = false
	@JsonField(name = ["statModifiers"]) var statModifiers: List<StatModifier> = emptyList()

}