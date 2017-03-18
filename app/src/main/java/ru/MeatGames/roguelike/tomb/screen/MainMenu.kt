package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Game
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.view.TextButton

class MainMenu(mContext: Context) : BasicScreen(mContext) {

    override val TAG: String = "Main Menu"

    private val mTitleTextPaint: Paint
    private val mNewGameButton: TextButton
    private val mExitGameButton: TextButton

    init {
        Global.game = mContext as Game

        mTitleTextPaint = ScreenHelper.getDefaultTextPaint(mContext)
        mTitleTextPaint.textSize = UnitConverter.convertSpToPixels(32F, context)

        mNewGameButton = TextButton(context, "Новая игра")
        mNewGameButton.mTextPaint.textAlign = Paint.Align.LEFT
        mNewGameButton.mDimensions = Rect(0,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth / 3,
                mScreenHeight)

        mExitGameButton = TextButton(context, "Выход")
        mExitGameButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mExitGameButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        canvas.drawText("Yet Another",
                (mScreenWidth / 2).toFloat(),
                (mScreenHeight / 8 * 3).toFloat(),
                mTitleTextPaint)
        canvas.drawText("Roguelike",
                (mScreenWidth / 2).toFloat(),
                (mScreenHeight / 8 * 3 + mScreenHeight / 16).toFloat(),
                mTitleTextPaint)
        mNewGameButton.draw(canvas)
        mExitGameButton.draw(canvas)
        //postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mNewGameButton.isPressed(touchX, touchY)) {
                    Global.game.newGame()
                }
                if (mExitGameButton.isPressed(touchX, touchY)) {
                    Global.game.exitGame()
                }
            }
        }
        return true
    }

}