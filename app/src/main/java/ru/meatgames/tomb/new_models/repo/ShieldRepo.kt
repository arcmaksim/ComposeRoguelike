package ru.meatgames.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.new_models.item.Shield

@JsonObject
class ShieldRepo {

	@JsonField(name = ["shields"]) var shields: List<Shield> = emptyList()


	fun getShild(itemId: Int): Shield = shields.first { it.id == itemId }

}