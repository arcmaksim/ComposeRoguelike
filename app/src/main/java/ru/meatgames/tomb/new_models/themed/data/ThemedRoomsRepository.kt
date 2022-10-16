package ru.meatgames.tomb.new_models.themed.data

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.meatgames.tomb.new_models.themed.data.room.ThemedRoomsDto
import ru.meatgames.tomb.new_models.themed.data.tile.ThemedTilesetsDto
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoom
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoomSymbolMapping
import ru.meatgames.tomb.new_models.themed.domain.room.toEntity
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurposeDefinition
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTileset
import ru.meatgames.tomb.new_models.themed.domain.tile.toEntity
import ru.meatgames.tomb.new_models.tile.GeneralTilePurpose

@OptIn(ExperimentalSerializationApi::class)
class ThemedRoomsRepository(
    private val context: Context,
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
