package ru.meatgames.tomb.view

import android.content.Context
import android.graphics.Rect
import android.view.View

abstract class BasicButton(context: Context?) : View(context) {

    abstract var mDimensions: Rect
    var mIsEnabled: Boolean = true

    fun isPressed(xCoordinate: Int, yCoordinate: Int) =
            if (mIsEnabled)
                mDimensions.contains(xCoordinate, yCoordinate)
            else
                false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
            setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))

    private fun measureWidth(measureSpec: Int): Int =
            getMeasurement(measureSpec, mDimensions.width())

    private fun measureHeight(measureSpec: Int): Int =
            getMeasurement(measureSpec, mDimensions.height())

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)
        var measurement = 0

        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> measurement = specSize
            MeasureSpec.AT_MOST -> Math.min(preferred, specSize)
            else -> measurement = preferred
        }

        return measurement
    }

}