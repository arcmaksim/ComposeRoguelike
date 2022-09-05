package ru.meatgames.tomb

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.IOException
import java.io.InputStream

object NewAssets {

    private val originalTileSize: Int = 24

    lateinit var tileset: ImageBitmap
    private lateinit var heroBitmaps: Array<ImageBitmap>

    fun loadAssets(
        context: Context,
    ) {
        tileset = context.getBitmapFromAsset("tiles").asImageBitmap()

        val temp = context.getBitmapFromAsset("character_animation_sheet")
        val heroSprites = Array(4) { i ->
            Bitmap.createBitmap(temp,
                i * originalTileSize,
                0,
                originalTileSize,
                originalTileSize
            )
        }
        heroBitmaps = heroSprites.map { it.asImageBitmap() }.toTypedArray()
    }

    fun getHeroBitmap(
        frame: Int,
    ): ImageBitmap = heroBitmaps[frame]

    private fun Context.getBitmapFromAsset(
        bitmapName: String,
    ): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("images/$bitmapName.png")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(inputStream)
    }

}