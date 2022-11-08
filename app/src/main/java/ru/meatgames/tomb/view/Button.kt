package ru.meatgames.tomb.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter

class Button(
        mContext: Context,
        var label: String = ""
) : BasicButton(mContext) {

    override lateinit var dimensions: Rect

    var backgroundPaint: Paint? = null
    var textPaint: Paint

    var padding: Float = UnitConverter.convertDpToPixels(24F, mContext)


    init {
        isFocusable = true
        isClickable = true

        textPaint = ScreenHelper.getDefaultTextPaint(mContext)
        textPaint.textSize = UnitConverter.convertSpToPixels(12F, mContext)
    }


    override fun onDraw(
            canvas: Canvas
    ) {
        if (!mIsEnabled) return
        backgroundPaint?.let { canvas.drawRect(dimensions, it) }
        when (textPaint.textAlign) {
            Paint.Align.CENTER -> canvas.drawText(
                        label,
                        dimensions.exactCenterX(),
                        dimensions.bottom - padding,
                        textPaint
                )
            Paint.Align.LEFT -> canvas.drawText(
                    label,
                        dimensions.left + padding,
                        dimensions.bottom - padding,
                        textPaint
            )
            Paint.Align.RIGHT -> canvas.drawText(
                    label,
                        dimensions.right - padding,
                        dimensions.bottom - padding,
                        textPaint
            )
        }
        invalidate()
    }

}