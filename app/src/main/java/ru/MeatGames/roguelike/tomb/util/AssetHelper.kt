package ru.MeatGames.roguelike.tomb.util

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream

class AssetHelper(val assetManager: AssetManager) {

    fun getBitmapFromAsset(bitmapName: String): Bitmap {
        var istr: InputStream? = null
        try {
            istr = assetManager.open("images/$bitmapName.png")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BitmapFactory.decodeStream(istr)
    }

}