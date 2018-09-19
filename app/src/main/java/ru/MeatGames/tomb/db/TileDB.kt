package ru.meatgames.tomb.db

import android.graphics.Rect

class TileDB(var mIsPassable: Boolean,
             var mIsTransparent: Boolean,
             var mIsUsable: Boolean) {

    var img: Rect? = null

}