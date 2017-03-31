package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.View
import ru.MeatGames.roguelike.tomb.MainActivity
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.FPSLogger
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.fillFrame

abstract class BasicScreen(context: Context) : View(context) {

    companion object {
        @JvmStatic private var mCurrentFrameNanoTime: Long = 0L
        @JvmStatic private var mPreviousFrameNanoTime: Long = System.nanoTime()
        @JvmStatic private var mCurrentFrameTime: Long = 0L
        @JvmStatic private var mFrameDeltaTime: Long = 0L

        @JvmStatic protected val mBackgroundPaint = Paint()
        @JvmStatic protected val mBitmapPaint = Paint()

        init {
            mBitmapPaint.isAntiAlias = false
            mBitmapPaint.isFilterBitmap = false
        }
    }

    abstract protected val TAG: String
    protected var mRecordFPS: Boolean = true

    protected val mScreenWidth: Int
    protected val mScreenHeight: Int

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        val screenSize = ScreenHelper.getScreenSize((context as MainActivity).windowManager)
        mScreenWidth = screenSize.x
        mScreenHeight = screenSize.y

        // TODO: move init to static init
        mBackgroundPaint.color = ContextCompat.getColor(getContext(), R.color.mainBackground)
    }

    override fun onDraw(canvas: Canvas?) {
        if (isFocused) {
            mCurrentFrameNanoTime = System.nanoTime()
            mCurrentFrameTime = System.currentTimeMillis()

            mFrameDeltaTime = mCurrentFrameNanoTime - mPreviousFrameNanoTime
            mPreviousFrameNanoTime = mCurrentFrameNanoTime

            if (mRecordFPS) FPSLogger.addEntry(mFrameDeltaTime)
        }

        drawScreen(canvas)
        invalidate()
    }

    abstract fun drawScreen(canvas: Canvas?)

    protected fun drawBackground(canvas: Canvas, backgroundPaint: Paint = mBackgroundPaint) {
        canvas.fillFrame(mScreenWidth, mScreenHeight, backgroundPaint)
    }

    fun getCurrentTime() = mCurrentFrameTime

    fun getCurrentNanoTime() = mCurrentFrameNanoTime

}