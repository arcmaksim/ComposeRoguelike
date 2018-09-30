package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class TileTextureJsonModel {

	@JsonField(name = ["frames"]) lateinit var tiles: List<TextureAtlasTile>


	@JsonObject
	class TextureAtlasTile {
		@JsonField(name = ["filename"]) lateinit var name: String
		@JsonField(name = ["frame"]) lateinit var frame: TextureAtlasImage
	}

	@JsonObject
	class TextureAtlasImage {
		@JsonField(name = ["x"]) var x: Int = 0
		@JsonField(name = ["y"]) var y: Int = 0
		@JsonField(name = ["w"]) var w: Int = 0
		@JsonField(name = ["h"]) var h: Int = 0

		fun toRect(): Rect = Rect(x, y, x+ w, y + h)
	}

}