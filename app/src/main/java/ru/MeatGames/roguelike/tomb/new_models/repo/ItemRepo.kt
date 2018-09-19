package ru.MeatGames.roguelike.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.MeatGames.roguelike.tomb.new_models.item.Item

@JsonObject
class ItemRepo {

	@JsonField(name = ["items"]) var items: List<Item> = emptyList()


	fun getItem(itemId: Int): Item = items.first { it.id == itemId }

}