package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.view.Button

class DeathScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Death Screen"

    private val mTextPaint: Paint
    private val mMainMenuButton: Button

    init {
        mMainMenuButton = Button(context, "В меню")
        mMainMenuButton.mDimensions = Rect(0,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = 24f
    }

    // TODO: refactor comments
    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)

        canvas.drawText(context.getString(R.string.death_from_label), mScreenWidth * 0.5F, mScreenHeight * 0.4F, mTextPaint)
        //canvas.drawBitmap(GameController.lastAttack, (mScreenWidth - GameController.lastAttack.width) * 0.5F, mScreenHeight * 0.425F, null)

        mMainMenuButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mMainMenuButton.isPressed(touchX, touchY)) {
                    GameController.gameOver()
                }
            }
        }
        return true
    }

}