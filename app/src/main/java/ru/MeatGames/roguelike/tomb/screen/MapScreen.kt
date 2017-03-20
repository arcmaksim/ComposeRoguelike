package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.MapHelper
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.view.TextButton

// displays explored map
class MapScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Map Screen"

    private val mDoorMarkerPaint = Paint()
    private val mRoomBackgroundPaint = Paint()
    private val mHeroMarkerPaint = Paint()
    private val mExitMarkerPaint = Paint()
    private val mTextPaint: Paint

    private val mBackButton: TextButton
    private val mMarkerSize: Float

    init {
        mDoorMarkerPaint.color = resources.getColor(R.color.hud)
        mRoomBackgroundPaint.color = resources.getColor(R.color.white)
        mHeroMarkerPaint.color = resources.getColor(R.color.fredl)
        mExitMarkerPaint.color = resources.getColor(R.color.grs)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = UnitConverter.convertSpToPixels(32F, context)

        mBackButton = TextButton(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mMarkerSize = (mScreenWidth / MapHelper.mapWidth).toFloat()
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        for (x in 0..MapHelper.mapWidth - 1)
            for (y in 0..MapHelper.mapHeight - 1) {
                if (Global.map!![x][y].mIsDiscovered) {
                    when (Global.map!![x][y].mObjectID) {
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
                    /*if(Global.map[x][y].hasMob())
                        canvas.drawRect(x*5,5+5*y,x*5+5,10+5*y,blue);*/
                }
            }
        canvas.drawRect(Global.hero!!.mx * mMarkerSize,
                (Global.hero!!.my + 1) * mMarkerSize,
                (Global.hero!!.mx + 1) * mMarkerSize,
                (Global.hero!!.my + 2) * mMarkerSize,
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
                    Global.game.changeScreen(Screens.GAME_SCREEN)
                }
            }
        }
        return true
    }
}