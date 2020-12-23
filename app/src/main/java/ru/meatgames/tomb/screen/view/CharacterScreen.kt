package ru.meatgames.tomb.screen.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.screen.BasicScreen
import ru.meatgames.tomb.screen.Screens
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.view.Button

class CharacterScreen(
        context: Context
) : BasicScreen(context) {

    override val TAG: String = "Character Screen"

    private val textPaint: Paint = ScreenHelper.getDefaultTextPaint(context).apply {
        textSize = 24f
        textAlign = Paint.Align.LEFT
    }
    private val backButton: Button = Button(context, resources.getString(R.string.back_label)).apply {
        textPaint.textAlign = Paint.Align.RIGHT
        dimensions = Rect(
                screenWidth / 3 * 2,
                screenHeight - screenHeight / 10,
                screenWidth,
                screenHeight
        )
    }
    private val textOffsetX: Float = screenWidth * 0.146F


    override fun drawScreen(
            canvas: Canvas
    ) {
        drawBackground(canvas)
        canvas.drawText("Уровень ${GameController.mHero.getStat(31)}", textOffsetX, screenHeight * 0.15F, textPaint)
        canvas.drawText("Сила ${GameController.mHero.getStat(0)}", textOffsetX, screenHeight * 0.2F, textPaint)
        canvas.drawText("Ловкость ${GameController.mHero.getStat(1)}", textOffsetX, screenHeight * 0.2375F, textPaint)
        canvas.drawText("Интеллект ${GameController.mHero.getStat(2)}", textOffsetX, screenHeight * 0.275F, textPaint)
        canvas.drawText("Выносливость ${GameController.mHero.getStat(3)}", textOffsetX, screenHeight * 0.3125F, textPaint)
        canvas.drawText("Восприятие ${GameController.mHero.getStat(4)}", textOffsetX, screenHeight * 0.35F, textPaint)
        canvas.drawText("Здоровье ${GameController.mHero.getStat(5)} / ${GameController.mHero.getStat(6)}", textOffsetX, screenHeight * 0.4F, textPaint)
        canvas.drawText("Мана ${GameController.mHero.getStat(7)} / ${GameController.mHero.getStat(8)}", textOffsetX, screenHeight * 0.4375F, textPaint)
        canvas.drawText("Запас сил ${GameController.mHero.getStat(9)} / ${GameController.mHero.getStat(10)}", textOffsetX, screenHeight * 0.475F, textPaint)
        canvas.drawText("Атака +${GameController.mHero.getStat(11)}", textOffsetX, screenHeight * 0.525F, textPaint)
        canvas.drawText("Урон ${GameController.mHero.getStat(12)} - ${GameController.mHero.getStat(13)}", textOffsetX, screenHeight * 0.5625F, textPaint)
        canvas.drawText("Защита ${GameController.mHero.getStat(19)}", textOffsetX, screenHeight * 0.6F, textPaint)
        canvas.drawText("Броня ${GameController.mHero.getStat(22)}", textOffsetX, screenHeight * 0.6375F, textPaint)

        backButton.draw(canvas)

        postInvalidate()
    }

    override fun onTouchEvent(
            event: MotionEvent
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (backButton.isPressed(touchX, touchY)) {
                    GameController.changeScreen(Screens.GAME_SCREEN)
                }
            }
        }
        return true
    }

}