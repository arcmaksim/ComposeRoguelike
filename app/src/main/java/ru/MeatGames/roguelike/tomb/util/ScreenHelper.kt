package ru.MeatGames.roguelike.tomb.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.view.WindowManager
import ru.MeatGames.roguelike.tomb.R

object ScreenHelper {

    @JvmStatic
    fun getScreenSize(windowManager: WindowManager): Point {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return size
    }

    @JvmStatic
    fun getDefaultTextPaint(context: Context): Paint {
        val mDefaultTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDefaultTextPaint.color = context.resources.getColor(R.color.white)
        mDefaultTextPaint.style = Paint.Style.FILL
        mDefaultTextPaint.textSize = 16f
        mDefaultTextPaint.textScaleX = 1f
        mDefaultTextPaint.textAlign = Paint.Align.CENTER
        mDefaultTextPaint.typeface = Typeface.createFromAsset(context.assets, "fonts/Bulgaria_Glorious_Cyr.ttf")
        return mDefaultTextPaint
    }

}