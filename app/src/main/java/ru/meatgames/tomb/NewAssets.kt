package ru.meatgames.tomb

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import java.io.IOException
import java.io.InputStream

private const val originalTileSize: Int = 24

object NewAssets {

    private lateinit var heroBitmaps: Array<ImageBitmap>

    val tileSize: IntSize = IntSize(24, 24)

    fun loadAssets(
        context: Context,
    ) {
        val charactedAnimationSheet = context.getBitmapFromAsset("character_animation_sheet")
        val heroSprites = Array(4) { i ->
            Bitmap.createBitmap(
                charactedAnimationSheet,
                i * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
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
