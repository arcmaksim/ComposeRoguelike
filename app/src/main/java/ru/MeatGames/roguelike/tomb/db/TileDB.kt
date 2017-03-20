package ru.MeatGames.roguelike.tomb.db

import android.graphics.Bitmap

class TileDB(var mIsPassable: Boolean,
             var mIsTransparent: Boolean,
             var mIsUsable: Boolean) {

    var img: Bitmap? = null

}