package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.model.Item
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

class DetailedItemScreen(context: Context,
                         selectedItem: Item) : BasicScreen(context) {

    override val TAG: String = "Detailed Item Screen"

    private val mMainTextPaint = ScreenHelper.getDefaultTextPaint(context)
    private val mSecondaryTextPaint = ScreenHelper.getDefaultTextPaint(context)

    private val mLeftSoftButton: Button
    private val mMiddleSoftButton: Button
    private val mBackButton: Button

    private var mSelectedItem: Item = selectedItem
    private var mSelectedItemRect: Rect
    private val mItemBitmapVerticalPadding = UnitConverter.convertDpToPixels(100F, context).toInt()
    private val mItemBitmapScale = 4

    private val mTextTopPadding: Float
    private val mTextLinePadding: Float

    init {
        mMainTextPaint.textSize = 24f

        mLeftSoftButton = Button(context, "")
        mLeftSoftButton.mTextPaint.textAlign = Paint.Align.LEFT
        mLeftSoftButton.mDimensions = Rect(0,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth / 3,
                mScreenHeight)

        mMiddleSoftButton = Button(context, resources.getString(R.string.drop_item_label))
        mMiddleSoftButton.mDimensions = Rect(mScreenWidth / 3,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth / 3 * 2,
                mScreenHeight)

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth,
                mScreenHeight)

        val selectedItemSize = Assets.mActualTileSize * mItemBitmapScale
        val itemBitmapHorizontalPadding = (mScreenWidth - selectedItemSize) / 2
        mSelectedItemRect = Rect(itemBitmapHorizontalPadding,
                mItemBitmapVerticalPadding,
                itemBitmapHorizontalPadding + selectedItemSize,
                mItemBitmapVerticalPadding + selectedItemSize)

        mTextTopPadding = mSelectedItemRect.bottom + mMainTextPaint.textSize * 2
        mTextLinePadding = mSecondaryTextPaint.textSize * 1.5F

        if (mSelectedItem.isConsumable) {
            mLeftSoftButton.mLabel = context.getString(R.string.use_label)
        } else {

            GameController.mHero.equipmentList[mSelectedItem.mType - 1]?.let {
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

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        drawItem(canvas)
        mLeftSoftButton.draw(canvas)
        mMiddleSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    private fun drawItem(canvas: Canvas) {
        canvas.drawBitmap(Assets.getItemImage(), mSelectedItem.image, mSelectedItemRect, mBitmapPaint)
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
            5 -> canvas.drawText("${Assets.stats[mSelectedItem.mValue1].mTitle} +${mSelectedItem.mValue2}", mScreenWidth * 0.5F, mTextTopPadding + mTextLinePadding, mSecondaryTextPaint)
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
                GameController.mHero.modifyStat(mSelectedItem.mValue1, mSelectedItem.mValue2, 1)
                GameController.updateLog("${mSelectedItem.mTitle} использован${mSelectedItem.mTitleEnding}")
                GameController.mHero.deleteItem(mSelectedItem)
            } else {
                GameController.mHero.equipmentList[mSelectedItem.mType - 1]?.let {
                    if (mSelectedItem == it) {
                        GameController.mHero.takeOffItem(it)
                    } else {
                        GameController.mHero.takeOffItem(it.mType - 1)
                        GameController.mHero.equipItem(mSelectedItem)
                    }
                } ?: let {
                    GameController.mHero.equipItem(mSelectedItem)
                }
            }
            GameController.changeScreen(Screens.GAME_SCREEN)
        }

        if (mMiddleSoftButton.isPressed(sx, sy)) {
            GameController.mHero.dropItem(mSelectedItem)
            GameController.vibrate()
            GameController.changeScreen(Screens.GAME_SCREEN)
        }

        if (mBackButton.isPressed(sx, sy)) {
            GameController.changeToLastScreen()
        }
    }

}