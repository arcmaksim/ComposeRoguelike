package ru.meatgames.tomb.new_models.room

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.new_models.tile.Tile

@JsonObject
class RoomRepo {

	@JsonField(name = ["rooms"]) lateinit var rooms: List<RoomJsonModel>


	@JsonObject
	class RoomJsonModel {
		@JsonField(name = ["width"]) var width: Int = 0
		@JsonField(name = ["height"]) var height: Int = 0
		@JsonField(name = ["floor"]) lateinit var floor: List<String>
		@JsonField(name = ["objects"]) lateinit var objects: List<String>
		@JsonField(name = ["floor_symbols"]) lateinit var floorSymbols: List<SymbolJsonModel>
		@JsonField(name = ["object_symbols"]) lateinit var objectSymbols: List<SymbolJsonModel>

		@JsonObject
		class SymbolJsonModel {
			@JsonField(name = ["symbol"]) lateinit var symbol: String
			@JsonField(name = ["meaning"], typeConverter = SymbolMeaningConverter::class)
			var meaning: Tile? = null
		}
	}

}