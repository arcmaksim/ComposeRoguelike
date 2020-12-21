package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.util.MapHelper
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

// displays explored map
class MapScreen(
        context: Context
) : BasicScreen(context) {

    override val TAG: String = "Map Screen"

    private val doorMarkerPaint = Paint().apply {
        color = ContextCompat.getColor(getContext(), R.color.hud)
    }
    private val roomBackgroundPaint = Paint().apply {
        color = ContextCompat.getColor(getContext(), R.color.white)
    }
    private val heroMarkerPaint = Paint().apply {
        color = ContextCompat.getColor(getContext(), R.color.fredl)
    }
    private val exitMarkerPaint = Paint().apply {
        color = ContextCompat.getColor(getContext(), R.color.grs)
    }
    private val textPaint: Paint = ScreenHelper.getDefaultTextPaint(context).apply {
        textSize = UnitConverter.convertSpToPixels(32F, context)
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
    private val markerSize: Float = (screenWidth / MapHelper.mMapWidth).toFloat()


    override fun drawScreen(
            canvas: Canvas
    ) {
        drawBackground(canvas)

        for (x in 0 until MapHelper.mMapWidth)
            for (y in 0 until MapHelper.mMapHeight) {
                if (!MapHelper.getMapTile(x, y)!!.mIsDiscovered) continue
                when (MapHelper.getMapTile(x, y)!!.mObjectID) {
                    0 -> canvas.drawRect(
                            x * markerSize,
                            (y + 1) * markerSize,
                            (x + 1) * markerSize,
                            (y + 2) * markerSize,
                            roomBackgroundPaint
                    )
                    31, 32 -> canvas.drawRect(
                            x * markerSize,
                            (y + 1) * markerSize,
                            (x + 1) * markerSize,
                            (y + 2) * markerSize,
                            doorMarkerPaint
                    )
                    40 -> canvas.drawRect(
                            x * markerSize,
                            (y + 1) * markerSize,
                            (x + 1) * markerSize,
                            (y + 2) * markerSize,
                            exitMarkerPaint
                    )
                }
                /*if(Assets.map[x][y].hasMob())
                    canvas.drawRect(x*5,5+5*y,x*5+5,10+5*y,blue);*/
            }
        canvas.drawRect(
                GameController.mHero.mx * markerSize,
                (GameController.mHero.my + 1) * markerSize,
                (GameController.mHero.mx + 1) * markerSize,
                (GameController.mHero.my + 2) * markerSize,
                heroMarkerPaint
        )
        backButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(
            event: MotionEvent
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
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