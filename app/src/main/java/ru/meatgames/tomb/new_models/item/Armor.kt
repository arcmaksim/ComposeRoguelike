package ru.meatgames.tomb.new_models.item

import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class Armor : InventoryItem() {

    companion object {
        const val LEATHER_ARMOR: Int = 1000
        const val CHAINMAIL: Int = 1001
        const val PLATE_ARMOR: Int = 1002
    }

}