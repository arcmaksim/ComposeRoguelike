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
class MapScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Map Screen"

    private val mDoorMarkerPaint = Paint()
    private val mRoomBackgroundPaint = Paint()
    private val mHeroMarkerPaint = Paint()
    private val mExitMarkerPaint = Paint()
    private val mTextPaint: Paint

    private val mBackButton: Button
    private val mMarkerSize: Float

    init {
        mDoorMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.hud)
        mRoomBackgroundPaint.color = ContextCompat.getColor(getContext(), R.color.white)
        mHeroMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.fredl)
        mExitMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.grs)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = UnitConverter.convertSpToPixels(32F, context)

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.textPaint.textAlign = Paint.Align.RIGHT
        mBackButton.dimensions = Rect(screenWidth / 3 * 2,
                screenHeight - screenHeight / 10,
                screenWidth,
                screenHeight)

        mMarkerSize = (screenWidth / MapHelper.mMapWidth).toFloat()
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)

        for (x in 0 until MapHelper.mMapWidth)
            for (y in 0 until MapHelper.mMapHeight) {
                if (MapHelper.getMapTile(x, y)!!.mIsDiscovered) {
                    when (MapHelper.getMapTile(x ,y)!!.mObjectID) {
                        0 -> canvas.drawRect(x * mMarkerSize,
                                (y + 1) * mMarkerSize,
                                (x + 1) * mMarkerSize,
                                (y + 2) * mMarkerSize,
                                mRoomBackgroundPaint)
                        31, 32 -> canvas.drawRect(x * mMarkerSize,
                                (y + 1) * mMarkerSize,
                                (x + 1) * mMarkerSize,
                                (y + 2) * mMarkerSize,
                                mDoorMarkerPaint)
                        40 -> canvas.drawRect(x * mMarkerSize,
                                (y + 1) * mMarkerSize,
                                (x + 1) * mMarkerSize,
                                (y + 2) * mMarkerSize,
                                mExitMarkerPaint)
                    }
                    /*if(Assets.map[x][y].hasMob())
                        canvas.drawRect(x*5,5+5*y,x*5+5,10+5*y,blue);*/
                }
            }
        canvas.drawRect(GameController.mHero.mx * mMarkerSize,
                (GameController.mHero.my + 1) * mMarkerSize,
                (GameController.mHero.mx + 1) * mMarkerSize,
                (GameController.mHero.my + 2) * mMarkerSize,
                mHeroMarkerPaint)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if(mBackButton.isPressed(touchX, touchY)) {
                    GameController.changeScreen(Screens.GAME_SCREEN)
                }
            }
        }
        return true
    }

}