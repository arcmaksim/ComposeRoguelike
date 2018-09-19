package ru.MeatGames.roguelike.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Shield {

	companion object {
		const val IRON_SHIELD: Int = 2000
		const val TOWER_SHIELD: Int = 2001
		const val DRAGON_SHIELD: Int = 2002
	}

	@JsonField(name = ["id"]) var id: Int = 0
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["ending"]) var ending: String = ""
	@JsonField(name = ["statModifiers"]) var statModifiers: List<StatModifier> = emptyList()

}