package ru.meatgames.tomb.screen

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.GameController
import ru.meatgames.tomb.InventoryFilterType
import ru.meatgames.tomb.R
import ru.meatgames.tomb.new_models.item.Weapon
import ru.meatgames.tomb.util.ScreenHelper
import ru.meatgames.tomb.util.UnitConverter
import ru.meatgames.tomb.view.Button

class GearScreen(context: Context) : BasicScreen(context) {

    override val TAG: String = "Gear Screen"

    private val mTextPaint: Paint = ScreenHelper.getDefaultTextPaint(context)
    private val gearPanelBackgroundPaint: Paint = Paint().apply {
        color = Color.parseColor("#90424242")
    }
    private val itemsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mPrimaryArmRect: Rect
    private val mSecondaryArmRect: Rect
    private val mPrimaryArmAltRect: Rect
    private val mBodyRect: Rect
    private val mGearRect: Rect

    private val itemRect: RectF = RectF()

    private val mLeftSoftButton: Button
    private val mBackButton: Button

    private val mIsTwoHandedWeaponEquipped: Boolean

    init {
        mLeftSoftButton = Button(context, resources.getString(R.string.inventory_label))
        mLeftSoftButton.textPaint.textAlign = Paint.Align.LEFT
        mLeftSoftButton.dimensions = Rect(0,
                (screenHeight * 0.9F).toInt(),
                screenWidth / 3,
                screenHeight)

        mBackButton = Button(context, resources.getString(R.string.back_label))
        mBackButton.textPaint.textAlign = Paint.Align.RIGHT
        mBackButton.dimensions = Rect(screenWidth / 3 * 2,
                (screenHeight * 0.9F).toInt(),
                screenWidth,
                screenHeight)

        val margin = UnitConverter.convertDpToPixels(16F, context).toInt()
        val cardWidth = (screenWidth - 4 * margin) / 3
        val cardHeight = UnitConverter.convertDpToPixels(160F, context).toInt()

        mPrimaryArmRect = Rect(margin, margin, cardWidth + margin, cardHeight + margin)
        mSecondaryArmRect = Rect(cardWidth + 2 * margin, margin, 2 * (cardWidth + margin), cardHeight + margin)
        mPrimaryArmAltRect = Rect(margin, margin, (cardWidth + margin) * 2, cardHeight + margin)
        mBodyRect = Rect(screenWidth - cardWidth - margin, margin, screenWidth - margin, cardHeight + margin)
        mGearRect = Rect(margin, cardHeight + 2 * margin, screenWidth - margin, (screenHeight * 0.9F).toInt() - margin)

        mIsTwoHandedWeaponEquipped = GameController.mHero.equipmentList[0] is Weapon &&
                (GameController.mHero.equipmentList[0] as Weapon).twoHanded
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        drawGear(canvas)
        mLeftSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        postInvalidate()
    }

    // TODO: refactor comments
    private fun drawGear(canvas: Canvas) {
        GameController.mHero.equipmentList[0]?.let {
            itemRect.left = mPrimaryArmRect.left.toFloat()
            itemRect.right = mPrimaryArmRect.right.toFloat()
            itemRect.top = mPrimaryArmRect.top + (mPrimaryArmRect.height() - mPrimaryArmRect.width()) * .5F
            itemRect.bottom = itemRect.top + mPrimaryArmRect.width()

            if (mIsTwoHandedWeaponEquipped) {
                canvas.drawRect(mPrimaryArmAltRect, gearPanelBackgroundPaint)
                canvas.drawBitmap(Assets.getItemImage(), it.image, itemRect, itemsPaint)

                itemRect.left = mSecondaryArmRect.left.toFloat()
                itemRect.right = mSecondaryArmRect.right.toFloat()
                canvas.drawBitmap(Assets.getItemImage(), it.image, itemRect, itemsPaint)
            } else {
                canvas.drawRect(mPrimaryArmRect, gearPanelBackgroundPaint)
                canvas.drawBitmap(Assets.getItemImage(), it.image, itemRect, itemsPaint)
            }
        } ?: let {
            canvas.drawRect(mPrimaryArmRect, gearPanelBackgroundPaint)
            canvas.drawText(context.getString(R.string.empty_label), mPrimaryArmRect.exactCenterX(), mPrimaryArmRect.exactCenterY(), mTextPaint)
        }

        if (!mIsTwoHandedWeaponEquipped) {
            canvas.drawRect(mSecondaryArmRect, gearPanelBackgroundPaint)
            GameController.mHero.equipmentList[1]?.let {
                itemRect.left = mSecondaryArmRect.left.toFloat()
                itemRect.right = mSecondaryArmRect.right.toFloat()
                itemRect.top = mSecondaryArmRect.top + (mSecondaryArmRect.height() - mSecondaryArmRect.width()) * .5F
                itemRect.bottom = itemRect.top + mSecondaryArmRect.width()

                canvas.drawBitmap(Assets.getItemImage(), it.image, itemRect, itemsPaint)
            } ?: let {
                canvas.drawText(context.getString(R.string.empty_label), mSecondaryArmRect.exactCenterX(), mSecondaryArmRect.exactCenterY(), mTextPaint)
            }
        }

        canvas.drawRect(mBodyRect, gearPanelBackgroundPaint)
        GameController.mHero.equipmentList[2]?.let {
            itemRect.left = mBodyRect.left.toFloat()
            itemRect.right = mBodyRect.right.toFloat()
            itemRect.top = mBodyRect.top + (mBodyRect.height() - mBodyRect.width()) * .5F
            itemRect.bottom = itemRect.top + mBodyRect.width()

            canvas.drawBitmap(Assets.getItemImage(), it.image, itemRect, itemsPaint)
        } ?: let {
            canvas.drawText(context.getString(R.string.empty_label), mBodyRect.exactCenterX(), mBodyRect.exactCenterY(), mTextPaint)
        }

        canvas.drawRect(mGearRect, gearPanelBackgroundPaint)
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
                //GameController.selectedItem = GameController.mHero.equipmentList[0]
                GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
            }
        } else {
            if (mPrimaryArmRect.contains(sx, sy)) {
                GameController.mHero.equipmentList[0]?.let {
                    GameController.selectedItem = it
                    GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
                } ?: let {
                    GameController.showInventoryWithFilters(InventoryFilterType.WEAPONS)
                }
            }

            if (mSecondaryArmRect.contains(sx, sy)) {
                GameController.mHero.equipmentList[1]?.let {
                    GameController.selectedItem = it
                    GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
                } ?: let {
                    GameController.showInventoryWithFilters(InventoryFilterType.SHIELDS)
                }
            }
        }


        if (mBodyRect.contains(sx, sy)) {
            GameController.mHero.equipmentList[2]?.let {
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