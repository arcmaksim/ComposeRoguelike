package ru.meatgames.tomb.model.room.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.meatgames.tomb.model.room.domain.Room
import ru.meatgames.tomb.model.room.domain.toEntity
import ru.meatgames.tomb.model.tile.data.FloorTileMapping
import ru.meatgames.tomb.model.tile.data.ObjectTileMapping
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class RoomsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun loadData(): RoomsData {
        val roomsData = Json.decodeFromStream<RoomsDto>(
            context.assets.open("data/rooms.json")
        )
        val rooms = roomsData.rooms.map { it.toEntity(roomsData.objectMapping) }

        return RoomsData(
            rooms = rooms,
            floorMapping = roomsData.floorMapping,
            objectMapping = roomsData.objectMapping,
        )
    }

}

class RoomsData(
    val rooms: List<Room>,
    val floorMapping: List<FloorTileMapping>,
    val objectMapping: List<ObjectTileMapping>,
)
