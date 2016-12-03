package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import ru.MeatGames.roguelike.tomb.Game
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.util.fillFrame
import ru.MeatGames.roguelike.tomb.view.TextButton

// displays explored map
class BrezenhamView(mContext: Context) : View(mContext) {

    val mScreenWidth: Int
    val mScreenHeight: Int

    private val mBackgroundPaint = Paint()
    private val mDoorMarkerPaint = Paint()
    private val mRoomBackgroundPaint = Paint()
    private val mHeroMarkerPaint = Paint()
    private val mExitMarkerPaint = Paint()
    private val mTextPaint: Paint

    private val mBackButton: TextButton

    private val mMarkerSize: Float

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        val screenSize = ScreenHelper.getScreenSize((context as Game).windowManager)
        mScreenWidth = screenSize.x
        mScreenHeight = screenSize.y

        mBackgroundPaint.color = resources.getColor(R.color.mainBackground)
        mDoorMarkerPaint.color = resources.getColor(R.color.hud)
        mRoomBackgroundPaint.color = resources.getColor(R.color.white)
        mHeroMarkerPaint.color = resources.getColor(R.color.fredl)
        mExitMarkerPaint.color = resources.getColor(R.color.grs)

        mTextPaint = ScreenHelper.getDefaultTextPaint(mContext)
        mTextPaint.textSize = UnitConverter.convertSpToPixels(32F, mContext)

        mBackButton = TextButton(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mMarkerSize = (mScreenWidth / Global.game!!.mw).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.fillFrame(mScreenWidth, mScreenHeight, mBackgroundPaint)
        for (x in 0..Global.game!!.mw - 1)
            for (y in 0..Global.game!!.mh - 1) {
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
                    Global.game!!.changeScreen(0)
                }
            }
        }
        return true
    }
}