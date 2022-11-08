package ru.meatgames.tomb.model.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.provider.GameDataProvider
import ru.meatgames.tomb.model.stat.Stat
import ru.meatgames.tomb.util.formatNumber

@JsonObject
class Weapon : InventoryItem() {

	companion object {
		const val KNIFE: Int = 0
		const val SHORT_SWORD: Int = 1
		const val LONG_SWORD: Int = 2
		const val GREATSWORD: Int = 3
	}

	@JsonField(name = ["isTwoHanded"]) var twoHanded: Boolean = false


	override fun getStatsDescription(): List<String> {
		val statsModifiersList = arrayListOf<String>()
		val tempModifiers = statModifiers.toMutableList()

		statsModifiersList.add(if (twoHanded) "Двуручное оружие" else "Одноручное оружие")

		tempModifiers.firstOrNull { it.id == Stat.ATTACK }?.let {
			statsModifiersList.add("${it.modifier.formatNumber()} ${GameDataProvider.stats.getStat(it.id).title}")
			tempModifiers.remove(it)
		}

		run {
			val minimumDamageModifier = tempModifiers.firstOrNull { it.id == Stat.MINIMUM_DAMAGE }
			val maximumDamageModifier = tempModifiers.firstOrNull { it.id == Stat.MAXIMUM_DAMAGE }

			if (minimumDamageModifier != null || maximumDamageModifier != null) {
				val minimumDamage = minimumDamageModifier?.modifier ?: 0
				val maximumDamage = maximumDamageModifier?.modifier ?: minimumDamage
				statsModifiersList.add("+ $minimumDamage - $maximumDamage Урон")
			}
		}

		tempModifiers.forEach {
			"${it.modifier.formatNumber()} ${GameDataProvider.stats.getStat(it.id).title}"
		}

		return statsModifiersList.toList()
	}

}
