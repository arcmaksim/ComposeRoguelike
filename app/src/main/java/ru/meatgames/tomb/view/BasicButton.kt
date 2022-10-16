package ru.meatgames.tomb.view

import android.content.Context
import android.graphics.Rect
import android.view.View

abstract class BasicButton(
        context: Context?
) : View(context) {

    abstract var dimensions: Rect
    var mIsEnabled: Boolean = true


    override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int
    ) = setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))

    fun isPressed(
            xCoordinate: Int,
            yCoordinate: Int
    ) = if (mIsEnabled) dimensions.contains(xCoordinate, yCoordinate) else false

    private fun measureWidth(
            measureSpec: Int
    ): Int = getMeasurement(measureSpec, dimensions.width())

    private fun measureHeight(
            measureSpec: Int
    ): Int = getMeasurement(measureSpec, dimensions.height())

    private fun getMeasurement(
            measureSpec: Int,
            preferred: Int
    ): Int = when (val specSize = MeasureSpec.getSize(measureSpec)) {
        MeasureSpec.EXACTLY -> specSize
        MeasureSpec.AT_MOST -> preferred.coerceAtMost(specSize)
        else -> preferred
    }

}