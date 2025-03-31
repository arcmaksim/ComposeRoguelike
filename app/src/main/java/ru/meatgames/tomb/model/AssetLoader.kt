package ru.meatgames.tomb.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetsLoader @Inject constructor(
    @ApplicationContext context: Context,
) {

    val illustrationAssets: IllustrationAssets

    init {
        val illustrationAtlas = context.getBitmapFromAsset("illustrations").asImageBitmap()
        illustrationAssets = IllustrationAssets(illustrationAtlas)
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

}
