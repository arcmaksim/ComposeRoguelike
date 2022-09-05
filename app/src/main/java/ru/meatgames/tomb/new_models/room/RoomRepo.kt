package ru.meatgames.tomb.new_models.room

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare

class RoomRepo(
    context: Context,
) {

    val rooms: List<Room> = LoganSquare.parse(
        context.assets.open("data/rooms.json"),
        RoomsDataJsonModel::class.java,
    ).roomsModels.map { it.toEntity() }

    fun getRandomRoom(): Room = rooms.first()

}