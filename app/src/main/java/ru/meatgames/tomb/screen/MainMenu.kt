package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

class MainMenu(mContext: Context) : BasicScreen(mContext) {

    override val TAG: String = "Main Menu"

    private val mTitleTextPaint: Paint = ScreenHelper.getDefaultTextPaint(mContext)
    private val mNewGameButton: Button
    private val mExitGameButton: Button

    init {
        mTitleTextPaint.textSize = UnitConverter.convertSpToPixels(32F, context)

        mNewGameButton = Button(context, "Новая игра")
        mNewGameButton.textPaint.textAlign = Paint.Align.LEFT
        mNewGameButton.dimensions = Rect(0,
                screenHeight - screenHeight / 10,
                screenWidth / 3,
                screenHeight)

        mExitGameButton = Button(context, "Выход")
        mExitGameButton.textPaint.textAlign = Paint.Align.RIGHT
        mExitGameButton.dimensions = Rect(screenWidth / 3 * 2,
                screenHeight - screenHeight / 10,
                screenWidth,
                screenHeight)
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        canvas.drawText("Yet Another",
                (screenWidth / 2).toFloat(),
                (screenHeight / 8 * 3).toFloat(),
                mTitleTextPaint)
        canvas.drawText("Roguelike",
                (screenWidth / 2).toFloat(),
                (screenHeight / 8 * 3 + screenHeight / 16).toFloat(),
                mTitleTextPaint)
        mNewGameButton.draw(canvas)
        mExitGameButton.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mNewGameButton.isPressed(touchX, touchY)) {
                    GameController.startNewGame()
                }
                if (mExitGameButton.isPressed(touchX, touchY)) {
                    GameController.exitGame()
                }
            }
        }
        return true
    }

}