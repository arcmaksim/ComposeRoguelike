package ru.meatgames.tomb.new_models.room

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonIgnore
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile

@JsonObject
class RoomsDataJsonModel {

    @JsonField(name = ["rooms"])
    lateinit var roomsModels: List<RoomJsonModel>

    @JsonObject
    class RoomJsonModel {
        @JsonField(name = ["width"])
        var width: Int = 0

        @JsonField(name = ["height"])
        var height: Int = 0

        @JsonField(name = ["floor"])
        lateinit var floor: List<String>

        @JsonField(name = ["objects"])
        lateinit var objects: List<String>

        @JsonField(name = ["floor_symbols"])
        lateinit var floorSymbols: List<SymbolJsonModel>

        @JsonField(name = ["object_symbols"])
        lateinit var objectSymbols: List<SymbolJsonModel>

        @JsonObject
        class SymbolJsonModel {
            @JsonField(name = ["symbol"])
            lateinit var symbol: String

            @JsonField(name = ["meaning"], typeConverter = SymbolMeaningConverter::class)
            var meaning: Tile = TileRepo.emptyTile

            @JsonField(name = ["isOuterWall"])
            var outerWall: Boolean = false
        }

        @JsonIgnore
        val floorTiles: HashMap<String, Tile> = HashMap()

        @JsonIgnore
        val objectTiles: HashMap<String, Tile> = HashMap()

        @JsonIgnore
        val outerTiles: ArrayList<Pair<Int, Int>> = ArrayList()

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

            for (y in objects.indices) {
                val objectLine = objects[y]
                for (x in objectLine.indices) {
                    if (outerSymbols.contains(objectLine[x].toString())) {
                        when (voidSymbol.symbol) {
                            objects.getOrElse(y - 1) { " " }.toString() -> outerTiles.add(x to y)
                            objectLine.getOrElse(x + 1) { ' ' }.toString() -> outerTiles.add(x to y)
                            objects.getOrElse(y + 1) { " " }.toString() -> outerTiles.add(x to y)
                            objectLine.getOrElse(x - 1) { ' ' }.toString() -> outerTiles.add(x to y)
                            /*
                            objects.getOrElse(y - 1) { "     " }.getOrElse(x) { '.' }.toString() == voidSymbol.symbol ->
                                addToOuterTiles(0, x, y)
                            objectLine.getOrElse(x + 1) { '.' }.toString() == voidSymbol.symbol ->
                                addToOuterTiles(1, x, y)
                            objects.getOrElse(y + 1) { "     " }.toString() == voidSymbol.symbol ->
                                addToOuterTiles(2, x, y)
                            objectLine.getOrElse(x - 1) { '.' }.toString() == voidSymbol.symbol ->
                                addToOuterTiles(3, x, y)
                             */
                        }
                    }
                }
            }
        }
    }

}

fun RoomsDataJsonModel.RoomJsonModel.toEntity(): Room = Room(
    width,
    height,
    floor.fold(emptyList()) { acc, string -> acc + string.asSequence() },
    objects.fold(emptyList()) { acc, string -> acc + string.asSequence() },
    floorTiles.mapKeys { it.key.first() },
    objectTiles.mapKeys { it.key.first() },
    outerTiles,
)