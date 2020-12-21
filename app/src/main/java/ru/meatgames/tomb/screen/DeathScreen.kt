package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.view.Button

class DeathScreen(
        context: Context
) : BasicScreen(context) {

    override val TAG: String = "Death Screen"

    private val textPaint: Paint = ScreenHelper.getDefaultTextPaint(context).apply {
        textSize = 24f
    }
    private val mainMenuButton: Button = Button(context, "В меню").apply {
        dimensions = Rect(
                0,
                screenHeight - screenHeight / 10,
                screenWidth,
                screenHeight
        )
    }


    // TODO: refactor comments
    override fun drawScreen(
            canvas: Canvas
    ) {
        drawBackground(canvas)

        canvas.drawText(context.getString(R.string.death_from_label), screenWidth * 0.5F, screenHeight * 0.4F, textPaint)
        //canvas.drawBitmap(GameController.lastAttack, (mScreenWidth - GameController.lastAttack.width) * 0.5F, mScreenHeight * 0.425F, null)

        mainMenuButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(
            event: MotionEvent
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mainMenuButton.isPressed(touchX, touchY)) {
                    GameController.gameOver()
                }
            }
        }
        return true
    }

}