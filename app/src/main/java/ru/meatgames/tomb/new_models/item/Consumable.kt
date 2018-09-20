package ru.meatgames.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Consumable : InventoryItem() {

    companion object {
        const val SMALL_HEALTH_POTION: Int = 3000
    }

}