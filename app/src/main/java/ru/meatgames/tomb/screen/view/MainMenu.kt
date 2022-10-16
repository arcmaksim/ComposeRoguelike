package ru.meatgames.tomb.screen.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.screen.BasicScreen
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

class MainMenu(
        context: Context
) : BasicScreen(context) {

    override val TAG: String = "Main Menu"

    private val titleTextPaint: Paint = ScreenHelper.getDefaultTextPaint(context).apply {
        textSize = UnitConverter.convertSpToPixels(32F, context)
    }
    private val newGameButton: Button = Button(context, "Новая игра").apply {
        textPaint.textAlign = Paint.Align.LEFT
        dimensions = Rect(
                0,
                screenHeight - screenHeight / 10,
                screenWidth / 3,
                screenHeight
        )
    }
    private val exitGameButton: Button = Button(context, "Выход").apply {
        textPaint.textAlign = Paint.Align.RIGHT
        dimensions = Rect(
                screenWidth / 3 * 2,
                screenHeight - screenHeight / 10,
                screenWidth,
                screenHeight
        )
    }


    override fun drawScreen(
            canvas: Canvas
    ) {
        drawBackground(canvas)
        canvas.drawText(
                "Yet Another",
                (screenWidth / 2).toFloat(),
                (screenHeight / 8 * 3).toFloat(),
                titleTextPaint
        )
        canvas.drawText(
                "Roguelike",
                (screenWidth / 2).toFloat(),
                (screenHeight / 8 * 3 + screenHeight / 16).toFloat(),
                titleTextPaint
        )
        newGameButton.draw(canvas)
        exitGameButton.draw(canvas)
    }

    override fun onTouchEvent(
            event: MotionEvent
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (newGameButton.isPressed(touchX, touchY)) {
                    GameController.startNewGame()
                }
                if (exitGameButton.isPressed(touchX, touchY)) {
                    GameController.exitGame()
                }
            }
        }
        return true
    }

}