package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Assets
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.*
import java.util.*

class GameScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "MainActivity Screen"

    // visible cells dimensions
    private val mMapViewportWidth = 9
    private val mMapViewportHeight = 9

    private val mMapBufferWidth = mMapViewportWidth + 2
    private val mMapBufferHeight = mMapViewportHeight + 2

    private val mMaxLogLines = 8
    var mGameEventsLog: LinkedList<String>

    private val mLightBluePaint = Paint()
    private val mDarkBluePaint = Paint()
    private val mSemiTransparentBackgroundPaint = Paint()
    private var mLightShadowPaint = Paint()
    private var mDarkShadowPaint = Paint()
    private val mMenuBackgroundPaint = Paint()
    private val mTextPaint: Paint

    var mObjectId: Int = 0 // tile id for ProgressBar
    var mProgressBarDuration: Int = 0
    var mProgressBarStartingTime: Long = 0

    var mHeroX: Int = 0
    var mHeroY: Int = 0
    var camx: Int = 0
    var camy: Int = 0
    var mDrawExitDialog = false
    var mDrawLog = true
    var mDrawProgressBar = false
    var mDrawWinScreen = false
    val mDrawActionsMenu = false

    var mActionCount: Int = 0 // currently not used

    val mActualTileSize = Assets.mActualTileSize
    val mScaleAmount = Assets.mScaleAmount

    private val mMapOffsetX: Int
    private val mMapOffsetY: Int

    // hero'single mIsPlayerMoved directions
    var mx: Int = 0
    var my: Int = 0

    val black: Paint

    var mInputLock = false // for handling long presses
    var mLongPressTime: Long = 0L
    val mLongPressTimeThreshold = 500 // in milliseconds
    var mIsLongPress = false

    val mMapBuffer = array2d(mMapBufferWidth, mMapBufferHeight) { MapBufferCell() }
    var mFloodedBuffer = array2d(mMapBufferWidth, mMapBufferHeight) { 0 }
    val mTileBuffer: Array<Array<Rect?>> = array2d(mMapViewportWidth, mMapViewportHeight) { null }
    val mHeroRect: Rect
    val mProgressBarRect: Rect

    init {
        mGameEventsLog = LinkedList()

        mLightBluePaint.color = ContextCompat.getColor(getContext(), R.color.lightBlue)
        mDarkBluePaint.color = ContextCompat.getColor(getContext(), R.color.darkBlue)
        mSemiTransparentBackgroundPaint.color = ContextCompat.getColor(getContext(), R.color.semiTransparentBackground)
        mLightShadowPaint.color = ContextCompat.getColor(getContext(), R.color.lightShadow)
        mDarkShadowPaint.color = ContextCompat.getColor(getContext(), R.color.darkShadow)
        mMenuBackgroundPaint.color = ContextCompat.getColor(getContext(), R.color.menuBackground)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textAlign = Paint.Align.LEFT

        black = Paint()
        black.color = resources.getColor(R.color.black)

        mHeroX = (mScreenWidth - mActualTileSize).div(2)
        mHeroY = (mScreenHeight - mActualTileSize).div(2)

        mMapOffsetX = (mScreenWidth - mActualTileSize * 9).div(2)
        mMapOffsetY = (mScreenHeight - mActualTileSize * 9).div(2)

        for (x in 0..mMapViewportWidth - 1) {
            for (y in 0..mMapViewportHeight - 1) {
                mTileBuffer[x][y] = Rect(x * mActualTileSize + mMapOffsetX,
                        y * mActualTileSize + mMapOffsetY,
                        (x + 1) * mActualTileSize + mMapOffsetX,
                        (y + 1) * mActualTileSize + mMapOffsetY)
            }
        }

        mHeroRect = Rect((mMapViewportWidth / 2) * mActualTileSize + mMapOffsetX,
                (mMapViewportHeight / 2) * mActualTileSize + mMapOffsetY,
                (mMapViewportWidth / 2 + 1) * mActualTileSize + mMapOffsetX,
                (mMapViewportHeight / 2 + 1) * mActualTileSize + mMapOffsetY)

        mProgressBarRect = Rect(mTileBuffer[mMapViewportWidth / 2 - 1][mMapViewportHeight / 2 - 1]!!.left,
                mTileBuffer[mMapViewportWidth / 2 - 1][mMapViewportHeight / 2 - 1]!!.top + mActualTileSize / 4,
                mTileBuffer[mMapViewportWidth / 2 + 1][mMapViewportHeight / 2 - 1]!!.right,
                mTileBuffer[mMapViewportWidth / 2 + 1][mMapViewportHeight / 2 - 1]!!.bottom - mActualTileSize / 4)
    }

    fun initProgressBar(objectId: Int, duration: Int) {
        mProgressBarDuration = duration
        mProgressBarStartingTime = getCurrentTime()
        mObjectId = objectId
        mDrawProgressBar = true
    }

    fun updateMapBuffer() {
        for (bufferX in 0..mMapBufferWidth - 1) {
            for (bufferY in 0..mMapBufferHeight - 1) {

                MapHelper.getMapTile(bufferX + camx - 1, bufferY + camy - 1)?.let {

                    mMapBuffer[bufferX][bufferY].mIsVisible = it.mCurrentlyVisible

                    mMapBuffer[bufferX][bufferY].mFloorID = it.mFloorID
                    mMapBuffer[bufferX][bufferY].mObjectID = it.mObjectID

                    mMapBuffer[bufferX][bufferY].mHasItem = it.hasItem()
                    mMapBuffer[bufferX][bufferY].mHasEnemy = it.hasMob()

                    val shadowX = bufferX - 5
                    val shadowY = bufferY - 5
                    val shadowSum = Math.abs(shadowX) + Math.abs(shadowY)

                    if ((shadowX == 0 || shadowY == 0) && shadowSum == 3 || shadowX != 0 && shadowY != 0 && shadowSum == 4) {
                        mMapBuffer[bufferX][bufferY].mShadowPaint = mLightShadowPaint
                    } else if ((shadowX == 0 || shadowY == 0) && shadowSum == 4 || (Math.abs(shadowX) != 0 || Math.abs(shadowY) != 0)
                            && shadowSum == 5 || Math.abs(shadowX) == Math.abs(shadowY) && Math.abs(shadowX) == 3) {
                        mMapBuffer[bufferX][bufferY].mShadowPaint = mDarkShadowPaint
                    }

                } ?: mMapBuffer[bufferX][bufferY].init()

            }
        }

        val floodedBuffer = floodBuffer()
        correctWalls(floodedBuffer)
    }

    private fun floodBuffer(): Array<Array<Int>> {
        val mapBufferFloodFill = array2d(mMapBufferWidth, mMapBufferHeight) { 0 }
        mapBufferFloodFill[mMapBufferWidth / 2][mMapBufferHeight / 2] = 1

        for (i in 1..mMapBufferWidth) {

            // we don't need to check edge cells
            for (bufferX in 1..mMapBufferWidth - 2) {
                for (bufferY in 1..mMapBufferHeight - 2) {

                    if (mapBufferFloodFill[bufferX][bufferY] == i && mMapBuffer[bufferX][bufferY].mObjectID != 1) {
                        for (ii in 0..8) {
                            if (bufferX + ii % 3 - 1 > -1
                                    && bufferX + ii % 3 - 1 < mMapBufferWidth - 1
                                    && bufferX + ii / 3 - 1 > -1
                                    && bufferX + ii / 3 - 1 < mMapBufferHeight - 1
                                    && mapBufferFloodFill[bufferX + ii % 3 - 1][bufferY + ii / 3 - 1] == 0) {
                                mapBufferFloodFill[bufferX + ii % 3 - 1][bufferY + ii / 3 - 1] = i + 1
                            }
                        }
                    }

                }
            }

        }

        for (bufferX in 0..mMapBufferWidth - 1) {
            var line = ""
            for (bufferY in 0..mMapBufferHeight - 1) {
                line += mapBufferFloodFill[bufferX][bufferY].toString() + " "
            }
            Log.d("Buffer", line)
        }

        return mapBufferFloodFill
    }

    private fun correctWalls(floodedBuffer: Array<Array<Int>>) {

        //drawMapInTheLog()
        mFloodedBuffer = floodedBuffer

        for (i in 1..mMapBufferWidth / 2) {
            // we don't need to check edge cells
            for (bufferX in 1..mMapBufferWidth - 2) {
                for (bufferY in 1..mMapBufferHeight - 2) {

                    if (mMapBuffer[bufferX][bufferY].mIsVisible && mMapBuffer[bufferX][bufferY].mObjectID == 1) {
                        val floodIndex = floodedBuffer[bufferX][bufferY]
                        mMapBuffer[bufferX][bufferY].mWallBitmap = 0

                        if (mMapBuffer[bufferX][bufferY - 1].mObjectID == 1
                                && Math.abs(floodedBuffer[bufferX][bufferY - 1] - floodIndex) < 2)
                                mMapBuffer[bufferX][bufferY].mWallBitmap += 1

                        if (mMapBuffer[bufferX + 1][bufferY].mObjectID == 1
                                && Math.abs(floodedBuffer[bufferX + 1][bufferY] - floodIndex) < 2)
                                mMapBuffer[bufferX][bufferY].mWallBitmap += 2

                        if (mMapBuffer[bufferX][bufferY + 1].mObjectID == 1
                                && Math.abs(floodedBuffer[bufferX][bufferY + 1] - floodIndex) < 2)
                                mMapBuffer[bufferX][bufferY].mWallBitmap += 4

                        if (mMapBuffer[bufferX - 1][bufferY].mObjectID == 1
                                && Math.abs(floodedBuffer[bufferX - 1][bufferY] - floodIndex) < 2)
                                mMapBuffer[bufferX][bufferY].mWallBitmap += 8

                    } else {
                        mMapBuffer[bufferX][bufferY].mWallBitmap = -1
                    }

                }
            }
        }
    }

    override fun drawScreen(canvas: Canvas?) {

        drawBackground(canvas!!)

        if (!mDrawWinScreen) {

            var animationFrame = (Math.abs(System.currentTimeMillis()) / 600 % 2).toInt()

            drawMap(canvas, animationFrame)
            if (!GameController.mHero.mIsFacingLeft) animationFrame += 2
            canvas.drawBitmap(Assets.getHeroSprite(animationFrame), null, mHeroRect, mBitmapPaint)

            drawHUD(canvas)

            if (GameController.mDrawInputAreas) drawLines(canvas)
            if (mDrawActionsMenu) drawActionsMenu(canvas, mActionCount)
            if (mDrawExitDialog) drawExitDialog(canvas)

            val currentXP = GameController.mHero.getStat(20).toFloat() / GameController.mHero.getStat(21)
            canvas.drawRect(4F, mScreenHeight - 11F, Math.round(mScreenWidth * 0.99F * currentXP).toFloat(), mScreenHeight - 4F, mDarkBluePaint)

            if (mDrawProgressBar) drawProgressBar(canvas)

        } else {
            drawFinalScreen(canvas)
        }

        if (mDrawLog) drawLog(canvas)

    }


    private fun drawMap(canvas: Canvas, animationFrame: Int) {

        var currentMapBufferCell: MapBufferCell

        for (x in 0..mMapViewportWidth - 1) {
            for (y in 0..mMapViewportHeight - 1) {

                // map buffer wider and higher than map viewport by 2
                currentMapBufferCell = mMapBuffer[x + 1][y + 1]

                if (currentMapBufferCell.mIsVisible) {

                    canvas.drawBitmap(Assets.getFloorImage(),
                            Assets.getAssetRect(currentMapBufferCell.mFloorID),
                            mTileBuffer[x][y],
                            mBitmapPaint)

                    if (currentMapBufferCell.mWallBitmap != -1) {
                        canvas.drawBitmap(Assets.getWallImage(currentMapBufferCell.mWallBitmap),
                                null,
                                mTileBuffer[x][y],
                                mBitmapPaint)
                    } else {
                        canvas.drawBitmap(Assets.getObjectImage(),
                                Assets.getAssetRect(currentMapBufferCell.mObjectID),
                                mTileBuffer[x][y],
                                mBitmapPaint)
                    }

                    /*if (currentMapBufferCell.mHasItem) {
                        canvas.drawBitmap(Assets.getItemImage(currentMapBufferCell.mI), null, mTileBuffer[x][y], mBitmapPaint)
                    }*/

                    //if (currentMapBufferCell.mHasEnemy) {
                    /*if (MapHelper.getMapTile(x, y)!!.hasMob()) {
                        canvas.drawBitmap(MapHelper.getMapTile(x, y)!!.mob.getImg(animationFrame), null, mTileBuffer[x][y], mBitmapPaint)
                    }*/

                    /*currentMapBufferCell.mShadowPaint?.let {
                        canvas.drawRect(currentPixelXtoDraw, currentPixelYtoDraw, leftDrawBorder, rightDrawBorder, it)
                    }*/
                }

                /*if (currentMapBufferCell.mObjectID == 1) {
                    canvas.drawText((mFloodedBuffer[cx + 1][cy + 1]).toString(), currentPixelXtoDraw + 16, currentPixelYtoDraw + 24, mTextPaint)
                }*/

            }
        }

    }

    private fun drawHUD(canvas: Canvas) {
        mTextPaint.textAlign = Paint.Align.CENTER

        canvas.drawBitmap(Assets.mInventoryIcon,
                (mScreenWidth / 8 - Assets.mInventoryIcon.width / 2).toFloat(),
                mScreenHeight * 0.9F + (mScreenHeight * 0.1F - Assets.mInventoryIcon.height) / 2,
                null)
        canvas.drawText("HP  ${GameController.mHero.getStat(5)} / ${GameController.mHero.getStat(6)}",
                mScreenWidth * 0.5F,
                mScreenHeight * 0.94F,
                mTextPaint)
        canvas.drawText("MP  ${GameController.mHero.getStat(7)} / ${GameController.mHero.getStat(8)}",
                mScreenWidth * 0.5F,
                mScreenHeight * 0.965F,
                mTextPaint)
        canvas.drawBitmap(Assets.mSkipTurnIcon,
                (mScreenWidth / 8 * 7 - Assets.mSkipTurnIcon.width / 2).toFloat(),
                mScreenHeight * 0.9F + (mScreenHeight * 0.1F - Assets.mSkipTurnIcon.height) / 2,
                null)
        mTextPaint.textAlign = Paint.Align.LEFT
    }

    // currently not used
    private fun drawActionsMenu(canvas: Canvas, n: Int) {
        val z = (360 / n).toFloat()
        val r = 190f
        var x: Int
        var y: Int
        canvas.fillFrame(mScreenWidth, mScreenHeight, mMenuBackgroundPaint)
        for (c in 0..n - 1) {
            x = (r * Math.cos(Math.toRadians((270 + z * c).toDouble()))).toInt()
            y = (r * Math.sin(Math.toRadians((270 + z * c).toDouble()))).toInt()
            canvas.drawRect((240 + x - 36).toFloat(), (400 + y - 36).toFloat(), (240 + x + 36).toFloat(), (400 + y + 36).toFloat(), black)
            canvas.drawRect((240 + x - 30).toFloat(), (400 + y - 30).toFloat(), (240 + x + 30).toFloat(), (400 + y + 30).toFloat(), mLightBluePaint)
        }
    }

    private fun drawLines(canvas: Canvas) {
        val temp = (mScreenHeight - (mScreenHeight + mScreenWidth) * 0.1F) / 3
        for (i in 1..2) {
            canvas.drawLine((mScreenWidth / 3 * i).toFloat(),
                    mScreenWidth * 0.1F,
                    (mScreenWidth / 3 * i).toFloat(),
                    mScreenHeight * 0.9F,
                    mBackgroundPaint)
            canvas.drawLine(0F,
                    temp * i + 48,
                    mScreenWidth.toFloat(),
                    temp * i + 48,
                    mBackgroundPaint)
        }
        canvas.drawLine(0F, mScreenWidth * 0.1F, mScreenWidth.toFloat(), mScreenWidth * 0.1F, mBackgroundPaint) // apparently it needs to be exactly width, not height
        canvas.drawLine(mScreenWidth * 0.5F, 0F, mScreenWidth * 0.5F, mScreenWidth * 0.1F, mBackgroundPaint)
        canvas.drawLine(0F, mScreenHeight * 0.9F, mScreenWidth.toFloat(), mScreenHeight * 0.9F, mBackgroundPaint)
        canvas.drawLine(mScreenWidth * 0.25F, mScreenHeight * 0.9F, mScreenWidth * 0.25F, mScreenHeight.toFloat(), mBackgroundPaint)
        canvas.drawLine(mScreenWidth * 0.75F, mScreenHeight * 0.9F, mScreenWidth * 0.75F, mScreenHeight.toFloat(), mBackgroundPaint)
    }

    private fun drawExitDialog(canvas: Canvas) {
        canvas.fillFrame(mScreenWidth, mScreenHeight, mSemiTransparentBackgroundPaint)
        canvas.drawRect(mScreenWidth * 0.05F, mScreenHeight * 0.4F, mScreenWidth * 0.95F, mScreenHeight * 0.59F, mMenuBackgroundPaint)
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 24f
        canvas.drawText(context.getString(R.string.exit_game_message), mScreenWidth * 0.5F, mScreenHeight * 0.46F, mTextPaint)
        mTextPaint.textSize = 16f
        canvas.drawText(context.getString(R.string.yes), mScreenWidth * 0.29F, mScreenHeight * 0.555F, mTextPaint)
        canvas.drawText(context.getString(R.string.No), mScreenWidth * 0.71F, mScreenHeight * 0.555F, mTextPaint)
    }

    private fun drawLog(canvas: Canvas) {
        mTextPaint.textSize = 16f
        mTextPaint.textAlign = Paint.Align.LEFT
        var count = 0
        mGameEventsLog.forEach {
            if (count < mMaxLogLines) {
                canvas.drawText(it, 5f, (20 + 21 * count++).toFloat(), mTextPaint)
            }
        }
    }

    // needs to be directly above the hero
    private fun drawProgressBar(canvas: Canvas) {
        if (getCurrentTime() - mProgressBarStartingTime > mProgressBarDuration) {
            mDrawProgressBar = false
            mDrawLog = true
            afterProgressBar(mObjectId)
            return
        }

        canvas.drawRect(mProgressBarRect, mTextPaint)
        canvas.drawRect(mProgressBarRect.left.toFloat(),
                mProgressBarRect.top.toFloat(),
                mProgressBarRect.left + (getCurrentTime() - mProgressBarStartingTime) * (mProgressBarRect.width() / mProgressBarDuration.toFloat()),
                mProgressBarRect.bottom.toFloat(),
                mLightBluePaint)

        mTextPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(context.getString(R.string.searching_label),
                mProgressBarRect.exactCenterX(),
                mProgressBarRect.exactCenterY() + mTextPaint.textSize / 2,
                mTextPaint)
    }

    private fun drawFinalScreen(canvas: Canvas) {
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 24f
        if (mDrawWinScreen) {
            canvas.drawText(context.getString(R.string.victory_label), mScreenWidth * 0.5F, mScreenHeight * 0.4F, mTextPaint)
            canvas.drawText(context.getString(R.string.king_was_slain_label), mScreenWidth * 0.5F, mScreenHeight * 0.45F, mTextPaint)
        }
        mTextPaint.textSize = 16f
        canvas.drawText(context.getString(R.string.to_menu_label), mScreenWidth * 0.5F, mScreenHeight * 0.95F, mTextPaint)
    }

    private fun afterProgressBar(result: Int) {
        when (result) {
            ObjectHelper.CHEST_OPENED -> {
                MapHelper.changeObject(GameController.mHero.mx + mx, GameController.mHero.my + my, ObjectHelper.CHEST_EMPTY)
                GameController.updateLog(context.getString(R.string.search_chest_message))
                GameController.createItem(GameController.mHero.mx + mx, GameController.mHero.my + my)
            }
            ObjectHelper.BOOKSHELF_FULL -> {
                MapHelper.changeObject(GameController.mHero.mx + mx, GameController.mHero.my + my, ObjectHelper.BOOKSHELF_EMPTY)
                addLine(context.getString(R.string.search_bookshelf_message))
                if (Random().nextInt(3) != 0) {
                    addLine(context.getString(R.string.experience_earned_message))
                    GameController.mHero.modifyStat(20, Random().nextInt(4) + 2, 1)
                } else {
                    addLine(context.getString(R.string.nothing_interesting_message))
                }
            }
        }
        updateMapBuffer()
    }

    fun clearLog() = mGameEventsLog.clear()

    fun addLine(message: String) = mGameEventsLog.add(message)

    private fun sign(x: Int) = if (x > 0) 1 else if (x < 0) -1 else 0

    fun line(xstart: Int, ystart: Int, xend: Int, yend: Int) {
        var x: Int
        var y: Int
        var dx: Int
        var dy: Int
        val incx: Int
        val incy: Int
        val pdx: Int
        val pdy: Int
        val es: Int
        val el: Int
        var err: Int
        var v = true

        dx = xend - xstart
        dy = yend - ystart
        incx = sign(dx)
        incy = sign(dy)

        if (dx < 0) dx = -dx
        if (dy < 0) dy = -dy

        if (dx > dy) {
            pdx = incx
            pdy = 0
            es = dy
            el = dx
        } else {
            pdx = 0
            pdy = incy
            es = dx
            el = dy
        }

        x = xstart
        y = ystart
        err = el / 2
        MapHelper.getMapTile(x, y)!!.mCurrentlyVisible = true

        for (t in 0..el - 1) {
            err -= es
            if (err < 0) {
                err += el
                x += incx
                y += incy
            } else {
                x += pdx
                y += pdy
            }

            if (MapHelper.horizontal(x) && MapHelper.vertical(y)) {
                if (!MapHelper.getMapTile(x, y)!!.mCurrentlyVisible) MapHelper.getMapTile(x, y)!!.mCurrentlyVisible = v
                if (v) MapHelper.getMapTile(x, y)!!.mIsDiscovered = true
                if (!MapHelper.getMapTile(x, y)!!.mIsTransparent) v = false
            }
        }
    }

    fun calculateLineOfSight(x: Int, y: Int) {
        val cm = if (camx < 0) 0 else camx
        val cm1 = if (camy < 0) 0 else camy
        for (c in cm..(if (cm + 9 >= MapHelper.mMapWidth) MapHelper.mMapWidth else cm + 9) - 1)
            for (c1 in cm1..(if (cm1 + 9 >= MapHelper.mMapWidth) MapHelper.mMapWidth else cm1 + 9) - 1)
                MapHelper.getMapTile(c, c1)!!.mCurrentlyVisible = false

        for (c in x - 1..x + 2 - 1)
            for (c1 in y - 1..y + 2 - 1)
                MapHelper.getMapTile(c, c1)!!.mCurrentlyVisible = true

        for (c in -1..1) {
            line(x, y, x + c, y - 4)
            line(x, y, x + c, y + 4)
        }

        for (c in -3..3) {
            line(x, y, x + c, y - 3)
            line(x, y, x + c, y + 3)
            line(x, y, x + c, y - 2)
            line(x, y, x + c, y + 2)
        }

        for (c in -4..-1 - 1) {
            line(x, y, x + c, y - 1)
            line(x, y, x + c, y + 1)
            line(x, y, x + Math.abs(c), y - 1)
            line(x, y, x + Math.abs(c), y + 1)
        }

        line(x, y, x - 4, y)
        line(x, y, x + 4, y)
    }

    private fun onTouchMain(touchX: Int, touchY: Int) {
        clearLog()

        if (touchY < mScreenWidth * 0.1F && touchX > mScreenWidth * 0.5F) {
            GameController.changeScreen(Screens.MAP_SCREEN)
        }

        if (touchY < mScreenWidth * 0.1F && touchX < mScreenWidth * 0.5F) {
            GameController.mDrawInputAreas = !GameController.mDrawInputAreas
        }

        val temp = (mScreenHeight - (mScreenHeight + mScreenWidth) * 0.1F) / 3
        if (touchY > mScreenWidth * 0.1F && touchY < mScreenHeight * 0.9F) {

            val x = touchX / (mScreenWidth / 3) - 1
            val y = ((touchY - mScreenWidth * 0.1F) / temp).toInt() - 1

            if (x == 0 && y == 0) {
                if (MapHelper.getObjectId(x, y) == 11) {
                    GameController.curLvls++
                    GameController.generateNewMap()
                    GameController.updateMapBuffer()
                    GameController.skipTurn()
                }
                MapHelper.getItem(GameController.mHero.mx, GameController.mHero.my)?.let {
                    val item = it
                    GameController.mHero.addItem(item)
                    addLine("${item.mTitle} подобран${item.mTitleEnding}")
                    GameController.vibrate()
                    GameController.skipTurn()
                }
            } else {
                GameController.move(x, y)
            }
        }

        if (touchY > mScreenHeight * 0.9F) {

            if (touchX < mScreenWidth * 0.25F) {
                GameController.changeScreen(Screens.INVENTORY_SCREEN)
            }

            if (touchX > mScreenWidth * 0.25F && touchX < mScreenWidth * 0.75F) {
                GameController.changeScreen(Screens.CHARACTER_SCREEN)
            }

            // TODO: refactor
            if (touchX > mScreenWidth * 0.75F) {
                if (GameController.mHero.mIsResting) {
                    GameController.mHero.interruptResting()
                } else {
                    if (mIsLongPress) {
                        if (GameController.mHero.isFullyHealed()) {
                            GameController.updateLog("Герой полностью здоров")
                        } else {
                            GameController.mHero.startResting()
                            GameController.skipTurn()
                        }
                        GameController.vibrate()
                    } else {
                        GameController.updateLog(context.getString(R.string.turn_passed_message))
                        GameController.skipTurn()
                    }
                }
            }
        }
    }

    private fun onTouchExitDialog(touchX: Int, touchY: Int) {
        if (touchY > mScreenHeight * 0.48F && touchY < mScreenHeight * 0.59F && touchX > mScreenWidth * 0.05F && touchX < mScreenWidth * 0.95F)
            if (touchX < mScreenWidth * 0.5F) {
                GameController.exitGame()
            } else {
                mDrawExitDialog = false
            }
    }

    private fun onTouchFinal(touchX: Int, touchY: Int) {
        if (touchY > mScreenHeight * 0.9F) {
            mDrawWinScreen = false
            GameController.startNewGame()
        }
    }

    private fun processTouchEvent(touchX: Int, touchY: Int) {
        if (!mDrawProgressBar && !mDrawWinScreen) {
            if (!mDrawExitDialog) {
                onTouchMain(touchX, touchY)
            } else {
                onTouchExitDialog(touchX, touchY)
            }
        }
        if (mDrawWinScreen) onTouchFinal(touchX, touchY)
        mIsLongPress = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (!mInputLock) {
                    if (GameController.mAcceptPlayerInput) {
                        val touchX = event.x.toInt()
                        val touchY = event.y.toInt()
                        processTouchEvent(touchX, touchY)
                    }
                } else {
                    mInputLock = false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                mLongPressTime = System.currentTimeMillis()
            }
            else -> {
                if (!mInputLock && System.currentTimeMillis() - mLongPressTime >= mLongPressTimeThreshold) {
                    mIsLongPress = true
                    mInputLock = true
                    val touchX = event.x.toInt()
                    val touchY = event.y.toInt()
                    processTouchEvent(touchX, touchY)
                }
            }
        }
        return true
    }

    inner class MapBufferCell {

        var mFloorID: Int = 0
        var mObjectID: Int = 0
        /*var mItemId: Int = 0
        var mCreatureId: Int = 0*/
        var mWallBitmap: Int = -1
        var mIsVisible: Boolean = false
        var mShadowPaint: Paint? = null
        var mHasItem: Boolean = false
        var mHasEnemy: Boolean = false

        init {
            init()
        }

        fun init() {
            mFloorID = 0
            mObjectID = 0
            /*mItemId = -1
            mCreatureId = -1*/
            mWallBitmap = -1
            mIsVisible = false
            mShadowPaint = null
            mHasItem = false
            mHasEnemy = false
        }

    }

}