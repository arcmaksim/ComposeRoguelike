package ru.meatgames.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.new_models.item.Consumable
import ru.meatgames.tomb.new_models.item.InventoryItem

@JsonObject
class ConsumableRepo {

	@JsonField(name = ["consumables"]) var items: List<Consumable> = emptyList()


	fun getItem(itemId: Int): InventoryItem = items.first { it.id == itemId }

}