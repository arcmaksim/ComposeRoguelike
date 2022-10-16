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

class CharacterScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Character Screen"

    private val mTextPaint: Paint
    private val mBackButton: Button
    private val mTextOffsetX: Float

    init {
        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = 24f
        mTextPaint.textAlign = Paint.Align.LEFT

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mTextOffsetX = mScreenWidth * 0.146F
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        canvas.drawText("Уровень ${GameController.mHero.getStat(31)}", mTextOffsetX, mScreenHeight * 0.15F, mTextPaint)
        canvas.drawText("Сила ${GameController.mHero.getStat(0)}", mTextOffsetX, mScreenHeight * 0.2F, mTextPaint)
        canvas.drawText("Ловкость ${GameController.mHero.getStat(1)}", mTextOffsetX, mScreenHeight * 0.2375F, mTextPaint)
        canvas.drawText("Интеллект ${GameController.mHero.getStat(2)}", mTextOffsetX, mScreenHeight * 0.275F, mTextPaint)
        canvas.drawText("Выносливость ${GameController.mHero.getStat(3)}", mTextOffsetX, mScreenHeight * 0.3125F, mTextPaint)
        canvas.drawText("Восприятие ${GameController.mHero.getStat(4)}", mTextOffsetX, mScreenHeight * 0.35F, mTextPaint)
        canvas.drawText("Здоровье ${GameController.mHero.getStat(5)} / ${GameController.mHero.getStat(6)}", mTextOffsetX, mScreenHeight * 0.4F, mTextPaint)
        canvas.drawText("Мана ${GameController.mHero.getStat(7)} / ${GameController.mHero.getStat(8)}", mTextOffsetX, mScreenHeight * 0.4375F, mTextPaint)
        canvas.drawText("Запас сил ${GameController.mHero.getStat(9)} / ${GameController.mHero.getStat(10)}", mTextOffsetX, mScreenHeight * 0.475F, mTextPaint)
        canvas.drawText("Атака +${GameController.mHero.getStat(11)}", mTextOffsetX, mScreenHeight * 0.525F, mTextPaint)
        canvas.drawText("Урон ${GameController.mHero.getStat(12)} - ${GameController.mHero.getStat(13)}", mTextOffsetX, mScreenHeight * 0.5625F, mTextPaint)
        canvas.drawText("Защита ${GameController.mHero.getStat(19)}", mTextOffsetX, mScreenHeight * 0.6F, mTextPaint)
        canvas.drawText("Броня ${GameController.mHero.getStat(22)}", mTextOffsetX, mScreenHeight * 0.6375F, mTextPaint)

        mBackButton.draw(canvas)

        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mBackButton.isPressed(touchX, touchY)) {
                    GameController.changeScreen(Screens.GAME_SCREEN)
                }
            }
        }
        return true
    }

}