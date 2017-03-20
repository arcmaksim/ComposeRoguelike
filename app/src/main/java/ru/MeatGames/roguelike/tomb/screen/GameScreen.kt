package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Game
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.MapHelper
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.fillFrame
import java.util.*

class GameScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Game Screen"

    // visible cells dimensions
    val mMapViewportWidth = 9
    val mMapViewportHeight = 9

    val mMaxLogLines = 8
    var mGameEventsLog: LinkedList<String>

    val mLightBluePaint = Paint()
    val mDarkBluePaint = Paint()
    val mSemiTransparentBackgroundPaint = Paint()
    var mLightShadowPaint = Paint()
    var mDarkShadowPaint = Paint()
    val mMenuBackgroundPaint = Paint()
    val mTextPaint: Paint

    var mTileID: Int = 0 // tile id for ProgressBar
    var mProgressBarDuration: Int = 0
    var mProgressBarStartingTime: Long = 0

    var camx: Int = 0
    var camy: Int = 0
    var mDrawExitDialog = false
    var mDrawLog = true
    var mDrawProgressBar = false
    var mDrawWinScreen = false
    val mDrawActionsMenu = false

    var mActionCount: Int = 0 // currently not used

    val mTileSize = Global.game.actualTileSize
    val mScaleAmount = Global.game.scaleAmount
    val mBitmapPaint = Paint()

    val mMapOffsetX: Float
    val mMapOffsetY: Float

    // hero'single mIsPlayerMoved directions
    var mx: Int = 0
    var my: Int = 0

    val black: Paint

    var mInputLock = false // for handling long presses
    var mLongPressTime: Long = 0L
    val mLongPressTimeThreshold = 500 // in milliseconds
    var mIsLongPress = false

    val mMapBuffer: Array<Array<MapBufferCell>> = array2d(mMapViewportWidth, mMapViewportHeight) { MapBufferCell() }

    inline fun <reified INNER> array2d(sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int)->INNER): Array<Array<INNER>>
            = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

    init {
        mGameEventsLog = LinkedList()

        mLightBluePaint.color = resources.getColor(R.color.lightBlue)
        mDarkBluePaint.color = resources.getColor(R.color.darkBlue)
        mSemiTransparentBackgroundPaint.color = resources.getColor(R.color.semiTransparentBackground)
        mLightShadowPaint.color = resources.getColor(R.color.lightShadow)
        mDarkShadowPaint.color = resources.getColor(R.color.darkShadow)
        mMenuBackgroundPaint.color = resources.getColor(R.color.menuBackground)

        mTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mTextPaint.textAlign = Paint.Align.LEFT

        black = Paint()
        black.color = resources.getColor(R.color.black)

        Global.hero!!.x = (mScreenWidth - mTileSize) / 2 / mScaleAmount
        Global.hero!!.y = (mScreenHeight - mTileSize) / 2 / mScaleAmount

        mBitmapPaint.isFilterBitmap = false

        mMapOffsetX = (mScreenWidth - mTileSize * 9) / 2 / mScaleAmount
        mMapOffsetY = (mScreenHeight - mTileSize * 9) / 2 / mScaleAmount
    }

    fun initProgressBar(tileID: Int, duration: Int) {
        mProgressBarDuration = duration
        mProgressBarStartingTime = Math.abs(System.currentTimeMillis()) / 10
        mTileID = tileID
        mDrawProgressBar = true
    }

    fun updateMapBuffer() {
        var mapX: Int
        var mapY: Int
        for (bufferX in 0..mMapViewportWidth - 1) {
            mapX = bufferX + camx
            for (bufferY in 0..mMapViewportHeight - 1) {
                mapY = bufferY + camy

                val mapCell = MapHelper.getMapCell(mapX, mapY)

                mMapBuffer[bufferX][bufferY].mIsVisible = mapCell?.mCurrentlyVisible ?: false

                if (mMapBuffer[bufferX][bufferY].mIsVisible) {
                    mMapBuffer[bufferX][bufferY].mFloorID = mapCell!!.mFloorID
                    mMapBuffer[bufferX][bufferY].mObjectID = mapCell.mObjectID

                    if (mMapBuffer[bufferX][bufferY].mObjectID == 30) {
                        mMapBuffer[bufferX][bufferY].mWallBitmap = 0
                    } else {
                        mMapBuffer[bufferX][bufferY].mWallBitmap = -1
                    }

                    mMapBuffer[bufferX][bufferY].mHasItem = mapCell.hasItem()
                    mMapBuffer[bufferX][bufferY].mHasEnemy = mapCell.hasMob()

                    val shadowX = bufferX - 4
                    val shadowY = bufferY - 4
                    val shadowSum = Math.abs(shadowX) + Math.abs(shadowY)

                    if ((shadowX == 0 || shadowY == 0) && shadowSum == 3 || shadowX != 0 && shadowY != 0 && shadowSum == 4) {
                        mMapBuffer[bufferX][bufferY].mShadowPaint = mLightShadowPaint
                    } else if ((shadowX == 0 || shadowY == 0) && shadowSum == 4 || (Math.abs(shadowX) != 0 || Math.abs(shadowY) != 0)
                            && shadowSum == 5 || Math.abs(shadowX) == Math.abs(shadowY) && Math.abs(shadowX) == 3) {
                        mMapBuffer[bufferX][bufferY].mShadowPaint = mDarkShadowPaint
                    }
                } else {
                    mMapBuffer[bufferX][bufferY].init()
                }
            }
        }

        correctWalls()
        correctCorners()
    }

    private fun correctWalls() {
        for (x in 0..mMapViewportWidth - 1) {
            for (y in 0..mMapViewportHeight - 1) {
                if (mMapBuffer[x][y].mWallBitmap == 0) {
                    val isConnectedToTheTop = (camy + y - 1 > -1 && Global.map!![camx + x][camy + y - 1].mObjectID == 30)
                    val isConnectedToTheRight = (camx + x + 1 < MapHelper.mapWidth && Global.map!![camx + x + 1][camy + y].mObjectID == 30)
                    val isConnectedToTheBottom = (camy + y + 1 < MapHelper.mapHeight && Global.map!![camx + x][camy + y + 1].mObjectID == 30)
                    val isConnectedToTheLeft = (camx + x - 1 > -1 && Global.map!![camx + x - 1][camy + y].mObjectID == 30)

                    val horizontalWall = (isConnectedToTheLeft && isConnectedToTheRight)
                    val verticalWall = (isConnectedToTheTop && isConnectedToTheBottom)

                    if (horizontalWall) {
                        mMapBuffer[x][y].mWallBitmap = 10
                    } else if (verticalWall) {
                        mMapBuffer[x][y].mWallBitmap = 5
                    } else {
                        if (isConnectedToTheTop) mMapBuffer[x][y].mWallBitmap += 1
                        if (isConnectedToTheRight) mMapBuffer[x][y].mWallBitmap += 2
                        if (isConnectedToTheBottom) mMapBuffer[x][y].mWallBitmap += 4
                        if (isConnectedToTheLeft) mMapBuffer[x][y].mWallBitmap += 8
                    }
                }
            }
        }
    }

    private fun correctCorners() {
        for (x in 0..mMapViewportWidth - 1) {
            for (y in 0..mMapViewportHeight - 1) {
                if (mMapBuffer[x][y].mWallBitmap % 3 == 0
                        && mMapBuffer[x][y].mWallBitmap % 3 == 0) {

                    if (mMapBuffer[x][y].mWallBitmap % 2 == 1) {
                        if (MapHelper.top(y - 1) && mMapBuffer[x][y - 1].mIsVisible) {
                            if (MapHelper.left(x - 1) && mMapBuffer[x - 1][y - 1].mIsVisible) {
                                mMapBuffer[x][y - 1].mWallBitmap = 12
                            } else if (MapHelper.right(x + 1) && mMapBuffer[x + 1][y - 1].mIsVisible) {
                                mMapBuffer[x][y - 1].mWallBitmap = 6
                            }
                        }
                    }

                    if (mMapBuffer[x][y].mWallBitmap and 0x2 == 1) {
                        if (MapHelper.right(x + 1) && mMapBuffer[x + 1][y].mIsVisible) {
                            if (MapHelper.top(y - 1) && mMapBuffer[x + 1][y - 1].mIsVisible) {
                                mMapBuffer[x + 1][y].mWallBitmap = 9
                            } else if (MapHelper.bottom(y + 1) && mMapBuffer[x + 1][y + 1].mIsVisible) {
                                mMapBuffer[x + 1][y].mWallBitmap = 12
                            }
                        }
                    }

                    if (mMapBuffer[x][y].mWallBitmap and 0x4 == 1) {
                        if (MapHelper.bottom(y + 1) && mMapBuffer[x][y + 1].mIsVisible) {
                            if (MapHelper.left(x - 1) && mMapBuffer[x - 1][y + 1].mIsVisible) {
                                mMapBuffer[x][y - 1].mWallBitmap = 9
                            } else if (MapHelper.right(x + 1) && mMapBuffer[x + 1][y + 1].mIsVisible) {
                                mMapBuffer[x][y - 1].mWallBitmap = 3
                            }
                        }
                    }

                    if (mMapBuffer[x][y].mWallBitmap and 0x8 == 1) {
                        if (MapHelper.left(x - 1) && mMapBuffer[x - 1][y].mIsVisible) {
                            if (MapHelper.top(y - 1) && mMapBuffer[x - 1][y - 1].mIsVisible) {
                                mMapBuffer[x - 1][y].mWallBitmap = 3
                            } else if (MapHelper.bottom(y + 1) && mMapBuffer[x - 1][y + 1].mIsVisible) {
                                mMapBuffer[x - 1][y].mWallBitmap = 6
                            }
                        }
                    }

                }
            }
        }
    }

    override fun drawScreen(canvas: Canvas?) {

        drawBackground(canvas!!)

        if (!mDrawWinScreen) {

            var animationFrame = (Math.abs(System.currentTimeMillis()) / 600 % 2).toInt()

            canvas.save()
            canvas.scale(mScaleAmount, mScaleAmount)

            drawMap(canvas, animationFrame)
            if (!Global.hero!!.mIsFacingLeft) {
                animationFrame += 2
            }
            canvas.drawBitmap(Global.getHeroSprite(animationFrame), Global.hero!!.x, Global.hero!!.y, mBitmapPaint)

            canvas.restore()

            drawHUD(canvas)

            if (Global.game.mDrawInputAreas) drawLines(canvas)
            if (mDrawActionsMenu) drawActionsMenu(canvas, mActionCount)
            if (mDrawExitDialog) drawExitDialog(canvas)

            val currentXP = Global.hero!!.getStat(20).toFloat() / Global.hero!!.getStat(21)
            canvas.drawRect(4F, mScreenHeight - 11F, Math.round(mScreenWidth * 0.99F * currentXP).toFloat(), mScreenHeight - 4F, mDarkBluePaint)

            canvas.save()
            canvas.scale(mScaleAmount, mScaleAmount)
            if (mDrawProgressBar) drawProgressBar(canvas)
            canvas.restore()

        } else {
            drawFinalScreen(canvas)
        }

        if (mDrawLog) drawLog(canvas)

    }


    private fun drawMap(canvas: Canvas, animationFrame: Int) {

        var currentMapBufferCell: MapBufferCell

        for (x in camx..camx + 9 - 1) {
            val cx = x - camx
            val currentPixelXtoDraw = (Global.game.mTileSize * cx + mMapOffsetX)
            val leftDrawBorder = (Global.game.mTileSize * (cx + 1) + mMapOffsetX)

            for (y in camy..camy + 9 - 1) {
                val cy = y - camy
                val currentPixelYtoDraw = (Global.game.mTileSize * cy + mMapOffsetY)
                val rightDrawBorder = (Global.game.mTileSize * (cy + 1) + mMapOffsetY)

                currentMapBufferCell = mMapBuffer[cx][cy]

                if (currentMapBufferCell.mIsVisible) {
                    canvas.drawBitmap(Global.map!![x][y].floorImg, currentPixelXtoDraw, currentPixelYtoDraw, mBitmapPaint)

                    if (currentMapBufferCell.mWallBitmap != -1) {
                        canvas.drawBitmap(Global.walls[currentMapBufferCell.mWallBitmap], currentPixelXtoDraw, currentPixelYtoDraw, mBitmapPaint)
                    } else {
                        canvas.drawBitmap(Global.map!![x][y].objectImg, currentPixelXtoDraw, currentPixelYtoDraw, mBitmapPaint)
                    }

                    if (currentMapBufferCell.mHasItem) {
                        canvas.drawBitmap(Global.map!![x][y].itemImg, currentPixelXtoDraw, currentPixelYtoDraw, mBitmapPaint)
                    }

                    //if (currentMapBufferCell.mHasEnemy) {
                    if (Global.map!![x][y].hasMob()) {
                        canvas.drawBitmap(Global.map!![x][y].mob.getImg(animationFrame), currentPixelXtoDraw, currentPixelYtoDraw, mBitmapPaint)
                    }

                    currentMapBufferCell.mShadowPaint?.let {
                        canvas.drawRect(currentPixelXtoDraw, currentPixelYtoDraw, leftDrawBorder, rightDrawBorder, currentMapBufferCell.mShadowPaint)
                    }
                }
            }
        }

    }

    private fun drawHUD(canvas: Canvas) {
        mTextPaint.textAlign = Paint.Align.CENTER

        canvas.drawBitmap(Global.game.mInventoryIcon,
                (mScreenWidth / 8 - Global.game.mInventoryIcon.width / 2).toFloat(),
                mScreenHeight * 0.9F + (mScreenHeight * 0.1F - Global.game.mInventoryIcon.height) / 2,
                null)
        canvas.drawText("HP  ${Global.hero!!.getStat(5)} / ${Global.hero!!.getStat(6)}",
                mScreenWidth * 0.5F,
                mScreenHeight * 0.94F,
                mTextPaint)
        canvas.drawText("MP  ${Global.hero!!.getStat(7)} / ${Global.hero!!.getStat(8)}",
                mScreenWidth * 0.5F,
                mScreenHeight * 0.965F,
                mTextPaint)
        canvas.drawBitmap(Global.game.mSkipTurnIcon,
                (mScreenWidth / 8 * 7 - Global.game.mSkipTurnIcon.width / 2).toFloat(),
                mScreenHeight * 0.9F + (mScreenHeight * 0.1F - Global.game.mSkipTurnIcon.height) / 2,
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
        if (Math.abs(System.currentTimeMillis()) / 10 - mProgressBarStartingTime > mProgressBarDuration) {
            mDrawProgressBar = false
            mDrawLog = true
            afterProgressBar(mTileID)
            return
        }
        val offsetX = Global.hero!!.x - mTileSize / mScaleAmount
        canvas.drawRect(offsetX,
                Global.hero!!.y - mTileSize * 0.75F / mScaleAmount,
                offsetX + mTileSize * 3 / mScaleAmount,
                Global.hero!!.y - mTileSize * 0.25F / mScaleAmount,
                mTextPaint)
        canvas.drawRect(offsetX,
                Global.hero!!.y - mTileSize * 0.75F / mScaleAmount,
                offsetX + (Math.abs(System.currentTimeMillis()) / 10 - mProgressBarStartingTime) * (mTileSize * 3F / mProgressBarDuration) / mScaleAmount,
                Global.hero!!.y - mTileSize * 0.25F / mScaleAmount,
                mLightBluePaint)

        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize /= mScaleAmount
        canvas.drawText(context.getString(R.string.searching_label), mScreenWidth * 0.5F / mScaleAmount, Global.hero!!.y - mTileSize * 0.35F / mScaleAmount, mTextPaint)
        mTextPaint.textSize *= mScaleAmount
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
            33 -> {
                Global.game.fillArea(Global.hero!!.mx + mx, Global.hero!!.my + my, 1, 1, Game.getFloor(Global.hero!!.mx + mx, Global.hero!!.my + my), 35)
                Global.mapview.addLine(context.getString(R.string.search_chest_message))
                Global.game.createItem(Global.hero!!.mx + mx, Global.hero!!.my + my)
            }
            36 -> {
                Global.game.fillArea(Global.hero!!.mx + mx, Global.hero!!.my + my, 1, 1, Game.getFloor(Global.hero!!.mx + mx, Global.hero!!.my + my), 37)
                addLine(context.getString(R.string.search_bookshelf_message))
                if (Global.game.mRandom.nextInt(3) != 0) {
                    addLine(context.getString(R.string.experience_earned_message))
                    Global.hero!!.modifyStat(20, Global.game.mRandom.nextInt(4) + 2, 1)
                } else {
                    addLine(context.getString(R.string.nothing_interesting_message))
                }
            }
        }
    }

    fun clearLog() = mGameEventsLog.clear()

    fun addLine(message: String) = mGameEventsLog.add(message)

    private fun sign(x: Int): Int {
        return if (x > 0) 1 else if (x < 0) -1 else 0
    }

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
        if (dx < 0)
            dx = -dx
        if (dy < 0)
            dy = -dy
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
        Global.map!![x][y].mCurrentlyVisible = true
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
            if (x > -1 && y > -1 && x < MapHelper.mapWidth && y < MapHelper.mapHeight) {
                if (!Global.map!![x][y].mCurrentlyVisible)
                    Global.map!![x][y].mCurrentlyVisible = v
                if (v)
                    Global.map!![x][y].mIsDiscovered = true
                if (!Global.map!![x][y].mIsTransparent)
                    v = false
            }
        }
    }

    fun calculateLineOfSight(x: Int, y: Int) {
        val cm = if (camx < 0) 0 else camx
        val cm1 = if (camy < 0) 0 else camy
        for (c in cm..(if (cm + 9 >= MapHelper.mapWidth) MapHelper.mapWidth else cm + 9) - 1)
            for (c1 in cm1..(if (cm1 + 9 >= MapHelper.mapWidth) MapHelper.mapWidth else cm1 + 9) - 1)
                Global.map!![c][c1].mCurrentlyVisible = false
        for (c in x - 1..x + 2 - 1)
            for (c1 in y - 1..y + 2 - 1)
                Global.map!![c][c1].mCurrentlyVisible = true
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
            Global.game.changeScreen(Screens.MAP_SCREEN)
        }

        if (touchY < mScreenWidth * 0.1F && touchX < mScreenWidth * 0.5F) {
            Global.game.mDrawInputAreas = !Global.game.mDrawInputAreas
        }

        val temp = (mScreenHeight - (mScreenHeight + mScreenWidth) * 0.1F) / 3
        if (touchY > mScreenWidth * 0.1F && touchY < mScreenHeight * 0.9F) {

            val x = touchX / (mScreenWidth / 3) - 1
            val y = ((touchY - mScreenWidth * 0.1F) / temp).toInt() - 1

            if (x == 0 && y == 0) {
                if (Global.map!![Global.hero!!.mx][Global.hero!!.my].mObjectID == 40) {
                    Game.curLvls++
                    Global.game.generateNewMap()
                    Global.game.skipTurn()
                }
                if (Global.map!![Global.hero!!.mx][Global.hero!!.my].hasItem()) {
                    val item = Global.map!![Global.hero!!.mx][Global.hero!!.my].item
                    Global.hero!!.addItem(item)
                    addLine("${item.mTitle} подобран${item.mTitleEnding}")
                    Global.vibrate()
                    Global.game.skipTurn()
                }
            } else {
                Global.game.move(x, y)
            }
        }

        if (touchY > mScreenHeight * 0.9F) {

            if (touchX < mScreenWidth * 0.25F) {
                Global.game.changeScreen(Screens.INVENTORY_SCREEN)
            }

            if (touchX > mScreenWidth * 0.25F && touchX < mScreenWidth * 0.75F) {
                Global.game.changeScreen(Screens.CHARACTER_SCREEN)
            }

            // TODO: refactor
            if (touchX > mScreenWidth * 0.75F) {
                if (Global.hero!!.mIsResting) {
                    Global.hero!!.interruptResting()
                } else {
                    if (mIsLongPress) {
                        if (Global.hero!!.isFullyHealed()) {
                            Global.mapview.addLine("Герой полностью здоров")
                        } else {
                            Global.hero!!.startResting()
                            Global.game.skipTurn()
                        }
                        Global.vibrate()
                    } else {
                        Global.mapview.addLine(context.getString(R.string.turn_passed_message))
                        Global.game.skipTurn()
                    }
                }
            }
        }
    }

    private fun onTouchExitDialog(touchX: Int, touchY: Int) {
        if (touchY > mScreenHeight * 0.48F && touchY < mScreenHeight * 0.59F && touchX > mScreenWidth * 0.05F && touchX < mScreenWidth * 0.95F)
            if (touchX < mScreenWidth * 0.5F) {
                Global.game.exitGame()
            } else {
                mDrawExitDialog = false
            }
    }

    private fun onTouchFinal(touchX: Int, touchY: Int) {
        if (touchY > mScreenHeight * 0.9F) {
            mDrawWinScreen = false
            Global.game.newGame()
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
                    if (Global.game.mAcceptPlayerInput) {
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
            mWallBitmap = -1
            mIsVisible = false
            mShadowPaint = null
            mHasItem = false
            mHasEnemy = false
        }

    }

}