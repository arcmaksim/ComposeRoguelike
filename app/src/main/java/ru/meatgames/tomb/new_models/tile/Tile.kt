package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect

class Tile(tileData: TilePropertyJsonModel, tileImageRect: Rect) {

	val passable: Boolean = tileData.passable
	val transparent: Boolean = tileData.transparent
	val usable: Boolean = tileData.usable
	val image: Rect = tileImageRect

}