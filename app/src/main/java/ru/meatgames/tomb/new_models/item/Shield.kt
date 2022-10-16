package ru.meatgames.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Shield : InventoryItem() {

    companion object {
        const val IRON_SHIELD: Int = 2000
        const val TOWER_SHIELD: Int = 2001
        const val DRAGON_SHIELD: Int = 2002
    }

}