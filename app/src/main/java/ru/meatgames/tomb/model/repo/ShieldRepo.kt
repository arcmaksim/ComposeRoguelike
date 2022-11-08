package ru.meatgames.tomb.model.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.item.Shield

@JsonObject
class ShieldRepo {

	@JsonField(name = ["shields"]) var shields: List<Shield> = emptyList()


	fun getShild(itemId: Int): Shield = shields.first { it.id == itemId }

}
