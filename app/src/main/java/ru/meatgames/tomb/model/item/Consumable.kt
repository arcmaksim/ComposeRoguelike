package ru.meatgames.tomb.model.item

import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Consumable : InventoryItem() {

    companion object {
        const val SMALL_HEALTH_POTION: Int = 3000
    }

}
