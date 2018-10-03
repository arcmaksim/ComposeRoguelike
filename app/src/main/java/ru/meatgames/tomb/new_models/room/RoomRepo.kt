package ru.meatgames.tomb.new_models.room

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare

class RoomRepo(context: Context) {

	val rooms: List<Room> = LoganSquare.parse(
			context.assets.open("data/rooms.json"),
			RoomsDataJsonModel::class.java)
			.getRooms()


	fun getRandomRoom(): Room {
		return rooms.first()
	}

}