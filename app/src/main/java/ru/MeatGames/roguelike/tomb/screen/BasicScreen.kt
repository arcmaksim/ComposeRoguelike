package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import ru.MeatGames.roguelike.tomb.Game
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.FPSLogger
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.fillFrame

abstract class BasicScreen(context: Context) : View(context) {

    abstract protected val TAG: String
    protected var mRecordFPS: Boolean = true

    protected val mScreenWidth: Int
    protected val mScreenHeight: Int

    protected val mBackgroundPaint = Paint()

    private var mPreviousFrameTime: Long = System.nanoTime()

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        val screenSize = ScreenHelper.getScreenSize((context as Game).windowManager)
        mScreenWidth = screenSize.x
        mScreenHeight = screenSize.y

        mBackgroundPaint.color = resources.getColor(R.color.mainBackground)
    }

    override fun onDraw(canvas: Canvas?) {
        val currentFrameTime = System.nanoTime()
        val frameTime = currentFrameTime - mPreviousFrameTime
        mPreviousFrameTime = currentFrameTime

        if (mRecordFPS) {
            FPSLogger.addEntry(frameTime)
        }

        drawScreen(canvas)
        invalidate()
    }

    abstract fun drawScreen(canvas: Canvas?)

    protected fun drawBackground(canvas: Canvas, backgroundPaint: Paint = mBackgroundPaint) {
        canvas.fillFrame(mScreenWidth, mScreenHeight, backgroundPaint)
    }

}