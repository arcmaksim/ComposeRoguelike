package ru.meatgames.tomb.new_models.room

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonIgnore
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile

@JsonObject
class RoomsDataJsonModel {

	@JsonField(name = ["rooms"]) lateinit var roomsModels: List<RoomJsonModel>


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
			var meaning: Tile = TileRepo.emptyTile
			@JsonField(name = ["isOuterWall"]) var outerWall: Boolean = false
		}


		@JsonIgnore val floorTiles: HashMap<String, Tile> = HashMap()
		@JsonIgnore val objectTiles: HashMap<String, Tile> = HashMap()
		@JsonIgnore val outerTiles: HashMap<Int, ArrayList<Pair<Int, Int>>> = HashMap()


		@OnJsonParseComplete
		fun processData() {
			for (floorSymbol in floorSymbols) {
				floorTiles[floorSymbol.symbol] = floorSymbol.meaning
			}
			for (objectSymbol in objectSymbols) {
				objectTiles[objectSymbol.symbol] = objectSymbol.meaning
			}

			val voidSymbol = objectSymbols.first { it.meaning == TileRepo.voidTile }
			val outerSymbols = objectSymbols.asSequence()
					.filter { it.outerWall }
					.map { it.symbol }
					.toSet()

			var objectsString = ""
			for (y in 0 until objects.size) {
				objectsString += objects[y]
			}

            val verticalPredicate = { index: Int ->
                objectsString.getOrElse(index) { ' ' }.toString() == voidSymbol.symbol
            }
            val horizontalPredicate = { lineIndex: Int, index: Int ->
				!(lineIndex in 0 until width && objectsString[index].toString() != voidSymbol.symbol)
            }

			for (i in 0 until objectsString.length) {
				val symbol = objectsString[i].toString()
				if (outerSymbols.contains(symbol)) {
					when {
                        verticalPredicate.invoke(i - width) -> addToOuterTiles(0, i % width, i / width)
                        horizontalPredicate.invoke(i % width + 1, i + 1) -> addToOuterTiles(1, i % width, i / width)
                        verticalPredicate.invoke(i + width) -> addToOuterTiles(2, i % width, i / width)
                        horizontalPredicate.invoke(i % width - 1, i - 1) -> addToOuterTiles(3, i % width, i / width)
					}
				}
			}
		}


		private fun addToOuterTiles(direction: Int, x: Int, y: Int) {
			val pair = x to y
			outerTiles[direction]?.add(pair) ?: let {
				outerTiles[direction] = arrayListOf(pair)
			}
		}
	}


	fun getRooms(): List<Room> {
		return List(roomsModels.size) { index ->
			val roomJsonModel = roomsModels[index]
			Room(roomJsonModel.width,
					roomJsonModel.height,
					roomJsonModel.floor,
					roomJsonModel.objects,
					roomJsonModel.floorTiles,
					roomJsonModel.objectTiles,
					roomJsonModel.outerTiles)
		}
	}

}