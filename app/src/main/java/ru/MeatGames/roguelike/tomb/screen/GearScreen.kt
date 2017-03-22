package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Assets
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.InventoryFilterType
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.view.TextButton

class GearScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Gear Screen"

    private val mTextPaint: Paint

    private val mPrimaryArmRect: Rect
    private val mSecondaryArmRect: Rect
    private val mPrimaryArmAltRect: Rect
    private val mBodyRect: Rect
    private val mGearRect: Rect

    private val mLeftSoftButton: TextButton
    private val mBackButton: TextButton

    private val mIsTwoHandedWeaponEquipped: Boolean

    init {
        mTextPaint = ScreenHelper.getDefaultTextPaint(context)

        mLeftSoftButton = TextButton(context, resources.getString(R.string.inventory_label))
        mLeftSoftButton.mTextPaint.textAlign = Paint.Align.LEFT
        mLeftSoftButton.mDimensions = Rect(0,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth / 3,
                mScreenHeight)

        mBackButton = TextButton(context, resources.getString(R.string.back_label))
        mBackButton.mTextPaint.textAlign = Paint.Align.RIGHT
        mBackButton.mDimensions = Rect(mScreenWidth / 3 * 2,
                (mScreenHeight * 0.9F).toInt(),
                mScreenWidth,
                mScreenHeight)

        val margin = UnitConverter.convertDpToPixels(16F, context).toInt()
        val cardWidth = (mScreenWidth - 4 * margin) / 3
        val cardHeight = UnitConverter.convertDpToPixels(160F, context).toInt()

        mPrimaryArmRect = Rect(margin, margin, cardWidth + margin, cardHeight + margin)
        mSecondaryArmRect = Rect(cardWidth + 2 * margin, margin, 2 * (cardWidth + margin), cardHeight + margin)
        mPrimaryArmAltRect = Rect(margin, margin, (cardWidth + margin) * 2, cardHeight + margin)
        mBodyRect = Rect(mScreenWidth - cardWidth - margin, margin, mScreenWidth - margin, cardHeight + margin)
        mGearRect = Rect(margin, cardHeight + 2 * margin, mScreenWidth - margin, (mScreenHeight * 0.9F).toInt() - margin)

        mIsTwoHandedWeaponEquipped = Assets.hero!!.equipmentList[0]?.mProperty ?: false
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        drawGear(canvas)
        mLeftSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    private fun drawGear(canvas: Canvas) {
        Assets.hero!!.equipmentList[0]?.let {
            if (mIsTwoHandedWeaponEquipped) {
                canvas.drawRect(mPrimaryArmAltRect, mBackgroundPaint)
                canvas.drawBitmap(it.image, mPrimaryArmAltRect.exactCenterX() - it.image.width / 2, mPrimaryArmAltRect.exactCenterY() - it.image.height / 2, null)
            } else {
                canvas.drawRect(mPrimaryArmRect, mBackgroundPaint)
                canvas.drawBitmap(it.image, mPrimaryArmRect.exactCenterX() - it.image.width / 2, mPrimaryArmRect.exactCenterY() - it.image.height / 2, null)
            }
        } ?: let {
            canvas.drawRect(mPrimaryArmRect, mBackgroundPaint)
            canvas.drawText(context.getString(R.string.empty_label), mPrimaryArmRect.exactCenterX(), mPrimaryArmRect.exactCenterY(), mTextPaint)
        }

        if (!mIsTwoHandedWeaponEquipped) {
            canvas.drawRect(mSecondaryArmRect, mBackgroundPaint)
            Assets.hero!!.equipmentList[1]?.let {
                canvas.drawBitmap(it.image, mSecondaryArmRect.exactCenterX() - it.image.width / 2, mSecondaryArmRect.exactCenterY() - it.image.width / 2, null)
            } ?: let {
                canvas.drawText(context.getString(R.string.empty_label), mSecondaryArmRect.exactCenterX(), mSecondaryArmRect.exactCenterY(), mTextPaint)
            }
        }

        canvas.drawRect(mBodyRect, mBackgroundPaint)
        Assets.hero!!.equipmentList[2]?.let {
            canvas.drawBitmap(it.image, mBodyRect.exactCenterX() - it.image.width / 2, mBodyRect.exactCenterY() - it.image.width / 2, null)
        } ?: let {
            canvas.drawText(context.getString(R.string.empty_label), mBodyRect.exactCenterX(), mBodyRect.exactCenterY(), mTextPaint)
        }

        canvas.drawRect(mGearRect, mBackgroundPaint)
        canvas.drawText(context.getString(R.string.empty_label), mGearRect.exactCenterX(), mGearRect.exactCenterY(), mTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchGear(touchX, touchY)
            }
        }
        return true
    }

    private fun onTouchGear(sx: Int, sy: Int) {
        // TODO: temporal solution
        if (mIsTwoHandedWeaponEquipped) {
            if (mPrimaryArmAltRect.contains(sx, sy)) {
                //GameController.selectedItem = Assets.hero!!.equipmentList[0]
                GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
            }
        } else {
            if (mPrimaryArmRect.contains(sx, sy)) {
                Assets.hero!!.equipmentList[0]?.let {
                    GameController.selectedItem = it
                    GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
                } ?: let {
                    GameController.showInventoryWithFilters(InventoryFilterType.WEAPONS)
                }
            }

            if (mSecondaryArmRect.contains(sx, sy)) {
                Assets.hero!!.equipmentList[1]?.let {
                    GameController.selectedItem = it
                    GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
                } ?: let {
                    GameController.showInventoryWithFilters(InventoryFilterType.SHIELDS)
                }
            }
        }


        if (mBodyRect.contains(sx, sy)) {
            Assets.hero!!.equipmentList[2]?.let {
                GameController.selectedItem = it
                GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
            } ?: let {
                GameController.showInventoryWithFilters(InventoryFilterType.ARMOR)
            }
        }

        if (mLeftSoftButton.isPressed(sx, sy)) {
            GameController.changeScreen(Screens.INVENTORY_SCREEN)
        }

        if (mBackButton.isPressed(sx, sy)) {
            GameController.changeScreen(Screens.GAME_SCREEN)
        }
    }

}