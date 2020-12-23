package ru.meatgames.tomb.new_models

import ru.meatgames.tomb.R
import ru.meatgames.tomb.new_models.tile.Tile

fun Tile.getFloorImage(): Int = when (name) {
    "FLOOR_0" -> R.mipmap.floor_0
    "FLOOR_1" -> R.mipmap.floor_1
    "FLOOR_CHECKED" -> R.mipmap.floor_checked
    else -> R.mipmap.floor_missing
}

/*
fun Tile.getObjectImage() {
    when (name) {
        "BOOKSHELF" -> "bookshelf"
        "BOOKSHELF_EMPTY" -> "bookshelf_empty"
        "CHAIR" -> "chair"
        "CHEST_CLOSED" -> "chest_closed"
        "CHEST_EMPTY" -> "chest_empty"
        "CHEST_OPENED" -> "chest_opened"
        "DOOR_CLOSED" -> "door_closed"
        "STAIRS_DOWN" -> "stairs_down"
        "STAIRS_UP" -> "stairs_up"
        "TABLE" -> "table"
        "TABLE_WITH_PAPERS" -> "table_with_papers"
        "WALL_0" -> "wall_0"
    }
}*/
