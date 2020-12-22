package ru.meatgames.tomb.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.view.WindowManager
import ru.meatgames.tomb.R

object ScreenHelper {

    @JvmStatic
    fun getScreenSize(
            windowManager: WindowManager
    ): Point = Point().also {
        windowManager.defaultDisplay.getSize(it)
    }

    @JvmStatic
    fun getDefaultTextPaint(
            context: Context
    ): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.resources.getColor(R.color.white)
        style = Paint.Style.FILL
        textSize = 16f
        textScaleX = 1f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.createFromAsset(context.assets, "fonts/bulgaria_glorious_cyr.ttf")
    }

}