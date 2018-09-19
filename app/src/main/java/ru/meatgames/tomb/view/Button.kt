package ru.meatgames.tomb.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter

class Button(mContext: Context, var mLabel: String = "") : BasicButton(mContext) {

    override lateinit var mDimensions: Rect

    var mBackgroundPaint: Paint? = null
    var mTextPaint: Paint

    var mPadding: Float = UnitConverter.convertDpToPixels(24F, mContext)

    init {
        isFocusable = true
        isClickable = true

        mTextPaint = ScreenHelper.getDefaultTextPaint(mContext)
        mTextPaint.textSize = UnitConverter.convertSpToPixels(12F, mContext)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mIsEnabled) {
            mBackgroundPaint?.let {
                canvas?.drawRect(mDimensions, mBackgroundPaint)
            }
            when(mTextPaint.textAlign) {
                Paint.Align.CENTER ->
                    canvas?.drawText(mLabel,
                            mDimensions.exactCenterX(),
                            mDimensions.bottom - mPadding,
                            mTextPaint)
                Paint.Align.LEFT ->
                    canvas?.drawText(mLabel,
                            mDimensions.left + mPadding,
                            mDimensions.bottom - mPadding,
                            mTextPaint)
                Paint.Align.RIGHT ->
                    canvas?.drawText(mLabel,
                            mDimensions.right - mPadding,
                            mDimensions.bottom - mPadding,
                            mTextPaint)
            }
            invalidate()
        }
    }

}