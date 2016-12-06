package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import ru.MeatGames.roguelike.tomb.Game
import ru.MeatGames.roguelike.tomb.Global
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.Screens
import ru.MeatGames.roguelike.tomb.model.Item
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.util.fillFrame
import ru.MeatGames.roguelike.tomb.view.TextButton

class DetailedItemScreen(context: Context, selectedItem: Item) : View(context) {

    private val mScreenWidth: Int
    private val mScreenHeight: Int

    private val mMainBackgroundPaint = Paint()
    private val mMainTextPaint: Paint
    private val mSecondaryTextPaint: Paint

    private val mLeftSoftButton: TextButton
    private val mMiddleSoftButton: TextButton
    private val mBackButton: TextButton

    private var mSelectedItem: Item
    private var mSelectedItemBitmap: Bitmap
    private val mItemBitmapVerticalPadding = UnitConverter.convertDpToPixels(100F, context)
    private val mItemBitmapHorizontalPadding: Float
    private val mItemBitmapSize: Int
    private val mItemBitmapScale = 3

    private val mTextTopPadding: Float
    private val mTextLinePadding: Float

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        mSelectedItem = selectedItem

        val screenSize = ScreenHelper.getScreenSize((context as Game).windowManager)
        mScreenWidth = screenSize.x
        mScreenHeight = screenSize.y

        mMainBackgroundPaint.color = resources.getColor(R.color.mainBackground)

        mMainTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mMainTextPaint.textSize = 24f

        mSecondaryTextPaint = ScreenHelper.getDefaultTextPaint(context)

        mLeftSoftButton = TextButton(context, "")
        mLeftSoftButton.mTextPaint.textAlign = Paint.Align.LEFT
        mLeftSoftButton.mDimensions = Rect(0,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth / 3,
                mScreenHeight)

        mMiddleSoftButton = TextButton(context, resources.getString(R.string.drop_item_label))
        mMiddleSoftButton.mDimensions = Rect(mScreenWidth / 3,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth / 3 * 2,
                mScreenHeight)

        mBackButton = TextButton(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth,
                mScreenHeight)

        mItemBitmapSize = mItemBitmapScale * Global.game.step
        mSelectedItemBitmap = Bitmap.createScaledBitmap(mSelectedItem.image, mItemBitmapSize, mItemBitmapSize, false)
        mItemBitmapHorizontalPadding = (mScreenWidth - mSelectedItemBitmap.width) * 0.5F

        mTextTopPadding = mItemBitmapVerticalPadding + mItemBitmapSize + mMainTextPaint.textSize * 2
        mTextLinePadding = mSecondaryTextPaint.textSize * 1.5F

        if (mSelectedItem.isConsumable) {
            mLeftSoftButton.mLabel = context.getString(R.string.use_label)
        } else {

            Global.hero!!.equipmentList[mSelectedItem.mType - 1]?.let {
                mLeftSoftButton.mLabel = if (mSelectedItem == it) {
                    context.getString(R.string.take_off_item_label)
                } else {
                    context.getString(R.string.change_equipped_item_label)
                }
            } ?: let {
                mLeftSoftButton.mLabel = context.getString(R.string.equip_item_label)
            }

        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.fillFrame(mScreenWidth, mScreenHeight, mMainBackgroundPaint)
        drawItem(canvas)
        mLeftSoftButton.draw(canvas)
        mMiddleSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    private fun drawItem(canvas: Canvas) {
        canvas.drawBitmap(mSelectedItemBitmap, mItemBitmapHorizontalPadding, mItemBitmapVerticalPadding, null)
        canvas.drawText(mSelectedItem.mTitle, mScreenWidth * 0.5F, mTextTopPadding, mMainTextPaint)
        var q = 1
        when (mSelectedItem.mType) {
            1 -> {
                if (!mSelectedItem.mProperty) {
                    canvas.drawText(context.getString(R.string.onehanded_weapon_label), mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q++, mSecondaryTextPaint)
                } else {
                    canvas.drawText(context.getString(R.string.twohanded_weapon_label), mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q++, mSecondaryTextPaint)
                }
                canvas.drawText("Атака +${mSelectedItem.mValue1}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q++, mSecondaryTextPaint)
                canvas.drawText("Урон ${mSelectedItem.mValue2} - ${mSelectedItem.mValue3}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q, mSecondaryTextPaint)
            }
            2, 3 -> {
                canvas.drawText("Защита ${mSelectedItem.mValue1}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q++, mSecondaryTextPaint)
                canvas.drawText("Броня ${mSelectedItem.mValue2}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding * q, mSecondaryTextPaint)
            }
            5 -> canvas.drawText("${Global.stats[mSelectedItem.mValue1].mTitle} +${mSelectedItem.mValue2}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding, mSecondaryTextPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchItem(touchX, touchY)
            }
        }
        return true
    }

    fun onTouchItem(sx: Int, sy: Int) {
        if (mLeftSoftButton.isPressed(sx, sy)) {
            if (mSelectedItem.isConsumable) {
                Global.hero!!.modifyStat(mSelectedItem.mValue1, mSelectedItem.mValue2, 1)
                Global.mapview.addLine("${mSelectedItem.mTitle} использован${mSelectedItem.mTitleEnding}")
                Global.hero!!.deleteItem(mSelectedItem)
            } else {
                Global.hero!!.equipmentList[mSelectedItem.mType - 1]?.let {
                    if (mSelectedItem == it) {
                        Global.hero!!.takeOffItem(it)
                    } else {
                        Global.hero!!.takeOffItem(it.mType - 1)
                        Global.hero!!.equipItem(mSelectedItem)
                    }
                } ?: let {
                    Global.hero!!.equipItem(mSelectedItem)
                }
            }
            Global.game.changeScreen(Screens.GAME_SCREEN)
        }

        if (mMiddleSoftButton.isPressed(sx, sy)) {
            Global.hero!!.dropItem(mSelectedItem)
            Game.v.vibrate(30)
            Global.game.changeScreen(Screens.GAME_SCREEN)
        }

        if (mBackButton.isPressed(sx, sy)) {
            Global.game.changeToLastScreen()
        }
    }

}