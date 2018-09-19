package ru.meatgames.tomb.db

import android.graphics.Rect

class ObjectDB(var mIsPassable: Boolean,
               var mIsTransparent: Boolean,
               var mIsUsable: Boolean,
               var mIsWall: Boolean) {

    var img: Rect? = null

}