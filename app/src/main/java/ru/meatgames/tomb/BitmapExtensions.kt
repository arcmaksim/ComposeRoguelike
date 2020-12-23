package ru.meatgames.tomb

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

fun Bitmap.getSubImage(
    copyRect: Rect
): Bitmap {
    val subImage = Bitmap.createBitmap(
        copyRect.width(),
        copyRect.height(),
        Bitmap.Config.ARGB_8888
    )
    Canvas(subImage).apply {
        drawBitmap(
            this@getSubImage,
            copyRect,
            Rect(0, 0, copyRect.width(), copyRect.height()),
            null
        )
    }
    return subImage
}