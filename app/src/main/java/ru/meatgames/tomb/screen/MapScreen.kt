package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import ru.meatgames.tomb.AreaGenerator
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.util.MapHelper
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button
import java.util.*

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

    private val areaGenerator: AreaGenerator = AreaGenerator()
    private lateinit var areas: List<Rect>
    private lateinit var areaColors: List<Triple<Int, Int, Int>>
    private val tempPaint: Paint = Paint()
    private val rnd = Random()


    init {
        mDoorMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.hud)
        mRoomBackgroundPaint.color = ContextCompat.getColor(getContext(), R.color.white)
        mHeroMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.fredl)
        mExitMarkerPaint.color = ContextCompat.getColor(getContext(), R.color.grs)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textSize = UnitConverter.convertSpToPixels(32F, context)

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                mScreenHeight - mScreenHeight / 10,
                mScreenWidth,
                mScreenHeight)

        mMarkerSize = (mScreenWidth / MapHelper.mMapWidth).toFloat()

        generateNewAreas()
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        drawAreas(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x.toInt()
                val touchY = event.y.toInt()
                if (mBackButton.isPressed(touchX, touchY)) {
                    GameController.changeScreen(Screens.GAME_SCREEN)
                } else {
                    generateNewAreas()
                }
            }
        }
        return true
    }


    private fun drawMap(canvas: Canvas) {
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
                }
            }
        canvas.drawRect(GameController.mHero.mx * mMarkerSize,
                (GameController.mHero.my + 1) * mMarkerSize,
                (GameController.mHero.mx + 1) * mMarkerSize,
                (GameController.mHero.my + 2) * mMarkerSize,
                mHeroMarkerPaint)
    }

    private fun drawAreas(canvas: Canvas) {
        for (i in 0 until areas.size) {
            val color = areaColors[i]
            val area = areas[i]
            tempPaint.setARGB(255, color.first, color.second, color.third)
            canvas.drawRect(area.left * mMarkerSize,
                    area.top * mMarkerSize,
                    area.right * mMarkerSize,
                    area.bottom * mMarkerSize,
                    tempPaint)
        }
    }

    private fun generateNewAreas() {
        areas = areaGenerator.generateAreas()
        areaColors = List(areas.size) {
            Triple(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }
    }

}