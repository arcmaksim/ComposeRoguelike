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

enum class ShadowSize {
    Small,
    Medium,
    Big,
}

object NewAssets {

    private lateinit var heroBitmaps: Array<ImageBitmap>
    private lateinit var shadowsBitmaps: Array<ImageBitmap>

    val tileSize: IntSize = IntSize(24, 24)

    fun loadAssets(
        context: Context,
    ) {
        val characterAnimationSheet = context.getBitmapFromAsset("character_animation_sheet")
        heroBitmaps = Array(4) { i ->
            Bitmap.createBitmap(
                characterAnimationSheet,
                i * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
            ).asImageBitmap()
        }
        
        val shadows = context.getBitmapFromAsset("shadows")
        shadowsBitmaps = Array(3) { i ->
            Bitmap.createBitmap(
                shadows,
                // ignoring first 3 "flying" shadows for now
                (i + 3) * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
            ).asImageBitmap()
        }
    }

    fun getHeroBitmap(
        frame: Int,
    ): ImageBitmap = heroBitmaps[frame]
    
    fun getShadow(
        size: ShadowSize,
    ) = when (size) {
        ShadowSize.Small -> shadowsBitmaps[0]
        ShadowSize.Medium -> shadowsBitmaps[1]
        ShadowSize.Big -> shadowsBitmaps[2]
    }

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
    
    fun Int.getOriginalTileSinglePixelOffset() = this / originalTileSize

}
