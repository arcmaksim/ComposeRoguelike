package ru.MeatGames.roguelike.tomb.db

import android.graphics.Bitmap

class ObjectDB(var mIsPassable: Boolean,
               var mIsTransparent: Boolean,
               var mIsUsable: Boolean,
               var mIsWall: Boolean) {

    var img: Bitmap? = null

}