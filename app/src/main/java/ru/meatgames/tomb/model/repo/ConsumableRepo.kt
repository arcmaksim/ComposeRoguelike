package ru.meatgames.tomb.model.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.item.Consumable
import ru.meatgames.tomb.model.item.InventoryItem

@JsonObject
class ConsumableRepo {

	@JsonField(name = ["consumables"]) var items: List<Consumable> = emptyList()


	fun getItem(itemId: Int): InventoryItem = items.first { it.id == itemId }

}
