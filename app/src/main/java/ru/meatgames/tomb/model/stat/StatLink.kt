package ru.meatgames.tomb.model.stat

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class StatLink {

	@JsonField(name = ["id"]) var id: String = ""
	@JsonField(name = ["type"], typeConverter = StatLinkTypeConverter::class) var type: StatLinkType = StatLinkType.CHILD

}