package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import ru.meatgames.tomb.MainActivity
import ru.meatgames.tomb.R
import ru.meatgames.tomb.util.FPSLogger
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.fillFrame

abstract class BasicScreen(
        context: Context
) : View(context) {

    companion object {
        @JvmStatic private var currentFrameNanoTime: Long = 0L
        @JvmStatic private var previousFrameNanoTime: Long = System.nanoTime()
        @JvmStatic private var currentFrameTime: Long = 0L
        @JvmStatic private var frameDeltaTime: Long = 0L
    }

    abstract protected val TAG: String
    protected var recordFPS: Boolean = true

    protected val screenWidth: Int
    protected val screenHeight: Int

    protected val backgroundPaint = Paint()
    protected val bitmapPaint = Paint().apply {
        isAntiAlias = false
        isFilterBitmap = false
    }


    init {
        isFocusable = true
        isFocusableInTouchMode = true

        val screenSize = ScreenHelper.getScreenSize((context as MainActivity).windowManager)
        screenWidth = screenSize.x
        screenHeight = screenSize.y

        // TODO: move init to static init
        backgroundPaint.color = ContextCompat.getColor(getContext(), R.color.mainBackground)
    }


    override fun onDraw(
            canvas: Canvas
    ) {
        if (isFocused) {
            currentFrameNanoTime = System.nanoTime()
            currentFrameTime = System.currentTimeMillis()

            frameDeltaTime = currentFrameNanoTime - previousFrameNanoTime
            previousFrameNanoTime = currentFrameNanoTime

            if (recordFPS) FPSLogger.addEntry(frameDeltaTime)
        }

        drawScreen(canvas)
        invalidate()
    }

    abstract fun drawScreen(canvas: Canvas)

    protected fun drawBackground(
            canvas: Canvas,
            backgroundPaint: Paint = this.backgroundPaint
    ) {
        canvas.fillFrame(screenWidth, screenHeight, backgroundPaint)
    }

    fun getCurrentTime() = currentFrameTime

    fun getCurrentNanoTime() = currentFrameNanoTime

}