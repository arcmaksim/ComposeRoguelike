package ru.MeatGames.roguelike.tomb.new_models.stat

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter

class StatLinkTypeConverter : StringBasedTypeConverter<StatLinkType>() {

	override fun convertToString(`object`: StatLinkType?): String {
		return `object`?.type ?: ""
	}

	override fun getFromString(string: String): StatLinkType {
		return StatLinkType.valueOf(string)
	}

}
