package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect

class Tile(
		private val tileData: TilePropertyJsonModel,
		val image: Rect
) {

	val name: String
		get() = tileData.name
	val passable: Boolean
		get() = tileData.passable
	val transparent: Boolean
		get() = tileData.transparent
	val usable: Boolean
		get() = tileData.usable

}