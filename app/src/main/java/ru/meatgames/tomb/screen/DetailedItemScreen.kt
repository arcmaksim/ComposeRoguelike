package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.R
import ru.meatgames.tomb.new_models.item.Consumable
import ru.meatgames.tomb.new_models.item.InventoryItem
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

class DetailedItemScreen(
        context: Context,
        selectedItem: InventoryItem
) : BasicScreen(context) {

    override val TAG: String = "DetailedItemScreen"

    private val mMainTextPaint = ScreenHelper.getDefaultTextPaint(context)
    private val mSecondaryTextPaint = ScreenHelper.getDefaultTextPaint(context)

    private val mLeftSoftButton: Button
    private val mMiddleSoftButton: Button
    private val mBackButton: Button

    private var mSelectedItem: InventoryItem = selectedItem
    private var mSelectedItemRect: Rect
    private val mItemBitmapVerticalPadding = UnitConverter.convertDpToPixels(100F, context).toInt()
    private val mItemBitmapScale = 4

    private val mTextTopPadding: Float
    private val mTextLinePadding: Float

    private var selectedItemProperties: List<String> = mSelectedItem.getStatsDescription()

    init {
        mMainTextPaint.textSize = 24f

        mLeftSoftButton = Button(context, "")
        mLeftSoftButton.textPaint.textAlign = Paint.Align.LEFT
        mLeftSoftButton.dimensions = Rect(0,
                (screenHeight * 0.9F).toInt(),
                screenWidth / 3,
                screenHeight)

        mMiddleSoftButton = Button(context, resources.getString(R.string.drop_item_label))
        mMiddleSoftButton.dimensions = Rect(screenWidth / 3,
                (screenHeight * 0.9F).toInt(),
                screenWidth / 3 * 2,
                screenHeight)

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.textPaint.textAlign = Paint.Align.RIGHT
        mBackButton.dimensions = Rect(screenWidth / 3 * 2,
                (screenHeight * 0.9F).toInt(),
                screenWidth,
                screenHeight)

        val selectedItemSize = Assets.mActualTileSize * mItemBitmapScale
        val itemBitmapHorizontalPadding = (screenWidth - selectedItemSize) / 2
        mSelectedItemRect = Rect(itemBitmapHorizontalPadding,
                mItemBitmapVerticalPadding,
                itemBitmapHorizontalPadding + selectedItemSize,
                mItemBitmapVerticalPadding + selectedItemSize)

        mTextTopPadding = mSelectedItemRect.bottom + mMainTextPaint.textSize * 2
        mTextLinePadding = mSecondaryTextPaint.textSize * 1.5F

        if (mSelectedItem is Consumable) {
            mLeftSoftButton.label = context.getString(R.string.use_label)
        } else {
            /*GameController.mHero.equipmentList[mSelectedItem.mType - 1]?.let {
                mLeftSoftButton.mLabel = if (mSelectedItem == it) {
                    context.getString(R.string.take_off_item_label)
                } else {
                    context.getString(R.string.change_equipped_item_label)
                }
            } ?: let {
                mLeftSoftButton.mLabel = context.getString(R.string.equip_item_label)
            }*/
        }
    }

    override fun drawScreen(
            canvas: Canvas
    ) {
        drawBackground(canvas)
        drawItem(canvas)
        //mLeftSoftButton.draw(canvas)
        //mMiddleSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    private fun drawItem(canvas: Canvas) {
        canvas.drawBitmap(Assets.getItemImage(), mSelectedItem.image, mSelectedItemRect, bitmapPaint)
        canvas.drawText(mSelectedItem.title, screenWidth * 0.5F, mTextTopPadding, mMainTextPaint)
        selectedItemProperties.forEachIndexed { index, property ->
            canvas.drawText(property, screenWidth * 0.5F, mTextTopPadding + mTextLinePadding * (index + 1), mSecondaryTextPaint)
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
        /*if (mLeftSoftButton.isPressed(sx, sy)) {
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
        }*/

        /* if (mMiddleSoftButton.isPressed(sx, sy)) {
             GameController.mHero.dropItem(mSelectedItem)
             GameController.vibrate()
             GameController.changeScreen(Screens.GAME_SCREEN)
         }*/

        if (mBackButton.isPressed(sx, sy)) {
            GameController.changeToLastScreen()
        }
    }

}