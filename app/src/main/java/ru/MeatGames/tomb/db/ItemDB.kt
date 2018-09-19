package ru.meatgames.tomb.db

import android.graphics.Rect
import ru.meatgames.tomb.model.Item

// TODO: reformat code - not very readable
class ItemDB(type: Int,
             title: String,
             titleEnding: String,
             value1: Int,
             value2: Int,
             value3: Int = 0,
             property: Boolean = false) : Item(type, title, titleEnding, value1, value2, value3, property) {

    var img: Rect? = null

}