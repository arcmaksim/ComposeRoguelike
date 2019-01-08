package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.new_models.repo.TileRepo
import ru.meatgames.tomb.new_models.tile.Tile
import ru.meatgames.tomb.util.*
import java.util.*

class GameScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "MainActivity Screen"

    // visible cells dimensions
    private val mMapViewportWidth = 9
    private val mMapViewportHeight = 9

    val mMapBufferWidth = mMapViewportWidth + 2
    val mMapBufferHeight = mMapViewportHeight + 2

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
    val mMapBuffer2 = Array(mMapBufferWidth * mMapBufferHeight) { MapBufferCell2() }
    var mFloodedBuffer = array2d(mMapBufferWidth, mMapBufferHeight) { 0 }
    val mTileBuffer: Array<Array<Rect?>> = array2d(mMapViewportWidth, mMapViewportHeight) { null }
    val mTileBuffer2: Array<Rect>
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

        for (x in 0 until mMapViewportWidth) {
            for (y in 0 until mMapViewportHeight) {
                mTileBuffer[x][y] = Rect(x * mActualTileSize + mMapOffsetX,
                        y * mActualTileSize + mMapOffsetY,
                        (x + 1) * mActualTileSize + mMapOffsetX,
                        (y + 1) * mActualTileSize + mMapOffsetY)
            }
        }

        mTileBuffer2 = Array(mMapViewportWidth * mMapViewportHeight) {
            val top = it / mMapViewportWidth * mActualTileSize + mMapOffsetY
            val left = it % mMapViewportWidth * mActualTileSize + mMapOffsetX
            Rect(left,
                    top,
                    left + mActualTileSize,
                    top + mActualTileSize)
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
        for (i in 0 until mMapBufferWidth * mMapBufferHeight) {
            GameController.getMapTile(i % mMapBufferWidth + camx - 1, i / mMapBufferWidth + camy - 1)?.let {

                mMapBuffer2[i].floorTile = it.floorTile
                mMapBuffer2[i].objectTile = if (it.objectTile.name != TileRepo.emptyTile.name &&
                        it.objectTile.name != TileRepo.voidTile.name) it.objectTile else null

                val shadowX = Math.abs(i % mMapBufferWidth - 5)
                val shadowY = Math.abs(i / mMapBufferWidth - 5)
                val shadowSum = shadowX + shadowY

                if ((shadowX == 0 || shadowY == 0) && shadowSum == 3 || shadowX != 0 && shadowY != 0 && shadowSum == 4) {
                    mMapBuffer2[i].mShadowPaint = mLightShadowPaint
                } else if ((shadowX == 0 || shadowY == 0) && shadowSum == 4 || (shadowX != 0 || shadowY != 0)
                        && shadowSum == 5 || shadowX == shadowY && shadowX == 3) {
                    mMapBuffer2[i].mShadowPaint = mDarkShadowPaint
                }
            } ?: mMapBuffer2[i].init()
        }
        GameController.updateLOS2()
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

        for (bufferX in 0 until mMapBufferWidth) {
            var line = ""
            for (bufferY in 0 until mMapBufferHeight) {
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
        for (x in 0 until mMapViewportWidth) {
            for (y in 0 until mMapViewportHeight) {
                val index = x + y * mMapViewportWidth
                // map buffer wider and higher than map viewport by 2
                val currentMapBufferTile = mMapBuffer2[x + 1 + (y + 1) * mMapBufferWidth]

                if (currentMapBufferTile.mIsVisible) {
                    currentMapBufferTile.floorTile?.let { floorTile ->
                        canvas.drawBitmap(Assets.tileset,
                                floorTile.image,
                                mTileBuffer2[index],
                                mBitmapPaint)
                    }
                    currentMapBufferTile.objectTile?.let { objectTile ->
                        canvas.drawBitmap(Assets.tileset,
                                objectTile.image,
                                mTileBuffer2[index],
                                mBitmapPaint)
                    }
                    currentMapBufferTile.mShadowPaint?.let {
                        canvas.drawRect(
                                mTileBuffer2[index].left.toFloat(),
                                mTileBuffer2[index].top.toFloat(),
                                mTileBuffer2[index].right.toFloat(),
                                mTileBuffer2[index].bottom.toFloat(),
                                it)
                    }
                }
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
        for (c in 0 until n) {
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

    fun castRay(xstart: Int, ystart: Int, xend: Int, yend: Int) {
        var x: Int = xstart
        var y: Int = ystart
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

        err = el / 2
        mMapBuffer2[x + y * mMapBufferWidth].mIsVisible = true

        for (t in 0 until el) {
            err -= es
            if (err < 0) {
                err += el
                x += incx
                y += incy
            } else {
                x += pdx
                y += pdy
            }

            if (x in 0..mMapBufferWidth && y in 0..mMapBufferHeight) {
                val currentTile = mMapBuffer2[x + y * mMapBufferWidth]
                if (!currentTile.mIsVisible) currentTile.mIsVisible = v
                if (currentTile.objectTile?.isTransparent == false) v = false
            }
        }
    }

    fun calculateLineOfSight(x: Int, y: Int) {
        for (i in 0 until mMapBufferWidth * mMapBufferHeight) {
            mMapBuffer2[i].mIsVisible = false
        }

        for (c in -1..1) {
            for (c1 in -1..1) {
                mMapBuffer2[c + mMapBufferWidth / 2 + (c1 + mMapBufferHeight / 2) * mMapBufferWidth].mIsVisible = true
            }
        }

        val xx = mMapBufferWidth / 2
        val yy = mMapBufferHeight / 2

        for (c in -1..1) {
            castRay(xx, yy, xx + c, yy - 4)
            castRay(xx, yy, xx + c, yy + 4)
        }

        for (c in -3..3) {
            castRay(xx, yy, xx + c, yy - 3)
            castRay(xx, yy, xx + c, yy + 3)
            castRay(xx, yy, xx + c, yy - 2)
            castRay(xx, yy, xx + c, yy + 2)
        }

        for (c in -4 until -1) {
            castRay(xx, yy, xx + c, yy - 1)
            castRay(xx, yy, xx + c, yy + 1)
            castRay(xx, yy, xx + Math.abs(c), yy - 1)
            castRay(xx, yy, xx + Math.abs(c), yy + 1)
        }

        castRay(xx, yy, xx - 4, yy)
        castRay(xx, yy, xx + 4, yy)
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
                // Item pickup
                /*MapHelper.getItem(GameController.mHero.mx, GameController.mHero.my)?.let {
                    val item = it
                    GameController.mHero.addItem(item)
                    addLine("${item.mTitle} подобран${item.mTitleEnding}")
                    GameController.vibrate()
                    GameController.skipTurn()
                }*/
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

    inner class MapBufferCell2 {

        var floorTile: Tile? = null
        var objectTile: Tile? = null
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