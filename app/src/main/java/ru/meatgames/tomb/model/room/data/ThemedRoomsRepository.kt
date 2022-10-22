package ru.meatgames.tomb.model.room.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.meatgames.tomb.model.room.domain.ThemedRoom
import ru.meatgames.tomb.model.room.domain.ThemedRoomSymbolMapping
import ru.meatgames.tomb.model.room.domain.toEntity
import ru.meatgames.tomb.model.tile.data.ThemedTilesetsDto
import ru.meatgames.tomb.model.tile.domain.ThemedTilePurposeDefinition
import ru.meatgames.tomb.model.tile.domain.ThemedTileset
import ru.meatgames.tomb.model.tile.domain.toEntity
import ru.meatgames.tomb.model.tile.domain.GeneralTilePurpose
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class ThemedRoomsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun loadData(): ThemedRoomsData {
        val tileData = Json.decodeFromStream<ThemedTilesetsDto>(
            context.assets.open("images/themed_tiles.json"),
        )

        val parsedTiles = tileData.themedTiles.map { it.toEntity() }
        val generalTiles = listOf(
            ThemedTilePurposeDefinition.General(
                purpose = GeneralTilePurpose.OpenDoor,
            ),
            ThemedTilePurposeDefinition.General(
                purpose = GeneralTilePurpose.ClosedDoor,
            )
        )

        val tiles = parsedTiles + generalTiles

        val tilesets = List(tileData.themes.size) { index ->
            val theme = tileData.themes[index]
            ThemedTileset(
                name = theme.name,
                verticalTileOffset = theme.verticalTileOffset,
            )
        }

        val roomsData = Json.decodeFromStream<ThemedRoomsDto>(
            context.assets.open("data/themed_rooms.json")
        )
        val mappings = roomsData.symbolMapping.map { it.toEntity() }
        val rooms = roomsData.rooms.map { it.toEntity(mappings) }

        return ThemedRoomsData(
            tilesets = tilesets,
            tiles = tiles,
            rooms = rooms,
            symbolMappings = mappings,
        )
    }

}

class ThemedRoomsData(
    val tilesets: List<ThemedTileset>,
    val tiles: List<ThemedTilePurposeDefinition>,
    val rooms: List<ThemedRoom>,
    val symbolMappings: List<ThemedRoomSymbolMapping>,
)
