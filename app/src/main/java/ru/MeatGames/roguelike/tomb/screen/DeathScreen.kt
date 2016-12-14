package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.view.TextButton

class DeathScreen(context: Context) : BasicScreen(context) {

    private val mTextPaint: Paint
    private val mMainMenuButton: TextButton

    init {
        mMainMenuButton = TextButton(context, "В меню")
        mMainMenuButton.mDimensions = Rect(0,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = 24f
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)

        canvas.drawText(context.getString(R.string.death_from_label), mScreenWidth * 0.5F, mScreenHeight * 0.4F, mTextPaint)
        canvas.drawBitmap(Global.game.lastAttack, (mScreenWidth - Global.game.lastAttack.width) * 0.5F, mScreenHeight * 0.425F, null)

        mMainMenuButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mMainMenuButton.isPressed(touchX, touchY)) {
                    Global.game.gameOver()
                }
            }
        }
        return true
    }
}