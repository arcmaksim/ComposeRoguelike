package ru.meatgames.tomb.new_models.tile

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class TilePropertyJsonModel {

	@JsonField(name = ["name"]) var name: String = ""
	@JsonField(name = ["passable"]) var passable: Boolean = false
	@JsonField(name = ["transparent"]) var transparent: Boolean = false
	@JsonField(name = ["usable"]) var usable: Boolean = false

    companion object {
        const val BOOKSHELF = "bookshelf"
        const val BOOKSHELF_EMPTY = "bookshelf_empty"
        const val CHAIR = "chair"
        const val CHEST_CLOSED = "chest_closed"
        const val CHEST_EMPTY = "chest_empty"
        const val CHEST_OPENED = "chest_opened"
        const val DOOR_CLOSED = "door_closed"
        const val FLOOR_0 = "floor_0"
        const val FLOOR_1 = "floor_1"
        const val FLOOR_CHECKED = "floor_checked"
        const val STAIRS_DOWN = "stairs_down"
        const val STAIRS_UP = "stairs_up"
        const val TABLE = "table"
        const val TABLE_WITH_PAPERS = "table_with_papers"
        const val WALL_0 = "wall_0"
    }

}