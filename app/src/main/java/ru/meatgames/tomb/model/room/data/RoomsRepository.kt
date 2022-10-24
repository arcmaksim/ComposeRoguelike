package ru.meatgames.tomb.model.room.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.room.domain.RoomSymbolMapping
import ru.meatgames.tomb.model.room.domain.toEntity
import ru.meatgames.tomb.model.tile.data.TilesetsDto
import ru.meatgames.tomb.model.tile.domain.TilePurposeDefinition
import ru.meatgames.tomb.model.tile.domain.Tileset
import ru.meatgames.tomb.model.tile.domain.toEntity
import ru.meatgames.tomb.model.tile.domain.GeneralTilePurpose
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class RoomsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun loadData(): RoomsData {
        val tileData = Json.decodeFromStream<TilesetsDto>(
            context.assets.open("images/themed_tiles.json"),
        )

        val parsedTiles = tileData.tilePurpose.map { it.toEntity() }
        val generalTiles = listOf(
            TilePurposeDefinition.General(
                purpose = GeneralTilePurpose.OpenDoor,
            ),
            TilePurposeDefinition.General(
                purpose = GeneralTilePurpose.ClosedDoor,
            )
        )

        val tiles = parsedTiles + generalTiles

        val tilesets = List(tileData.themes.size) { index ->
            val theme = tileData.themes[index]
            Tileset(
                name = theme.name,
                verticalTileOffset = theme.verticalTileOffset,
            )
        }

        val roomsData = Json.decodeFromStream<RoomsDto>(
            context.assets.open("data/themed_rooms.json")
        )
        val mappings = roomsData.symbolMapping.map { it.toEntity() }
        val rooms = roomsData.rooms.map { it.toEntity(mappings) }

        return RoomsData(
            tilesets = tilesets,
            tiles = tiles,
            rooms = rooms,
            symbolMappings = mappings,
        )
    }

}

class RoomsData(
    val tilesets: List<Tileset>,
    val tiles: List<TilePurposeDefinition>,
    val rooms: List<Room>,
    val symbolMappings: List<RoomSymbolMapping>,
)
