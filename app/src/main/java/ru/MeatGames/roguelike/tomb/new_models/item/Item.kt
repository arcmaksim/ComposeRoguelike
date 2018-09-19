package ru.MeatGames.roguelike.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Item {

	companion object {
		const val SMALL_HEALTH_POTION: Int = 3000
	}

	@JsonField(name = ["id"]) var id: Int = 0
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["ending"]) var ending: String = ""
	@JsonField(name = ["statModifiers"]) var statModifiers: List<StatModifier> = emptyList()

}