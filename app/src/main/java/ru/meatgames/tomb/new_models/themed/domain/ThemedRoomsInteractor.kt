package ru.meatgames.tomb.new_models.themed.domain

import ru.meatgames.tomb.new_models.themed.data.ThemedRoomsRepository
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoom
import ru.meatgames.tomb.new_models.themed.domain.room.ThemedRoomSymbolMapping
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurposeDefinition
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTileset

class ThemedRoomsInteractor(
    private val repo: ThemedRoomsRepository,
) {

    val availableTilesets: List<ThemedTileset>
    val availableRooms: List<ThemedRoom>

    private val mappings: List<ThemedRoomSymbolMapping>
    private val tiles: List<ThemedTilePurposeDefinition>

    init {
        val loadedData = repo.loadData()

        availableTilesets = loadedData.tilesets
        availableRooms = loadedData.rooms
        mappings = loadedData.symbolMappings
        tiles = loadedData.tiles
    }

    fun getRoom(
        room: ThemedRoom,
        tileset: ThemedTileset,
    ): ThemedRoomBlueprint = ThemedRoomBlueprint(
        room = room,
        tileset = tileset,
        tiles = tiles,
        mappings = mappings,
    )

    fun getRandomRoom(
        room: ThemedRoom = availableRooms.random(),
        tileset: ThemedTileset = availableTilesets.random(),
    ): ThemedRoomBlueprint = ThemedRoomBlueprint(
        room = room,
        tileset = tileset,
        tiles = tiles,
        mappings = mappings,
    )

}

class ThemedRoomBlueprint(
    val tileset: ThemedTileset,
    val room: ThemedRoom,
    val tiles: List<ThemedTilePurposeDefinition>,
    val mappings: List<ThemedRoomSymbolMapping>,
)
