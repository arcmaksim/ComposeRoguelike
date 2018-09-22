package ru.meatgames.tomb.new_models.tile

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class TilePropertiesJsonModel {

	@JsonField(name = ["tiles"]) var tiles: List<TilePropertyJsonModel> = emptyList()

}