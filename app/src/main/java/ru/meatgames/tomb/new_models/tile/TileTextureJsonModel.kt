package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class TileTextureJsonModel {

	@JsonField(name = ["frames"]) var tiles: List<TextureAtlasTile> = emptyList()


	@JsonObject
	class TextureAtlasTile {

		@JsonField(name = ["filename"]) var name: String = ""
		@JsonField(name = ["frame"], typeConverter = RectConverter::class)
		lateinit var frame: Rect

	}

}