package ru.meatgames.tomb.model.item

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class StatModifier {

	@JsonField(name = ["id"]) var id: String = ""
	@JsonField(name = ["modifier"]) var modifier: Int = 0

}