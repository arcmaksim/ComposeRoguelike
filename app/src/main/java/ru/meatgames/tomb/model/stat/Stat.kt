package ru.meatgames.tomb.model.stat

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Stat {

	companion object {
		const val STRENGTH: String = "strength"
        const val AGILITY: String = "agility"
        const val INTELLECT: String = "intellect"
        const val PHYSIQUE: String = "physique"
        const val CURRENT_HEALTH: String = "currentHealth"
        const val MAXIMUM_HEALTH: String = "maximumHealth"
        const val MINIMUM_DAMAGE: String = "minimumDamage"
        const val MAXIMUM_DAMAGE: String = "maximumDamage"
        const val ATTACK: String = "attack"
        const val DEFENSE: String = "defense"
        const val ARMOR: String = "armor"
        const val SPEED: String = "speed"
	}

	@JsonField(name = ["id"]) var id: String = ""
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["links"]) var links: List<StatLink> = emptyList()

}
