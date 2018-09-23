package ru.meatgames.tomb.new_models.room

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.tile.Tile

class SymbolMeaningConverter : StringBasedTypeConverter<Tile?>() {

	override fun convertToString(`object`: Tile?): String = ""

	override fun getFromString(meaning: String?): Tile? {
		return if (meaning == "nothing") null else GameDataProvider.tiles.tiles.first { it.name == meaning }
	}

}