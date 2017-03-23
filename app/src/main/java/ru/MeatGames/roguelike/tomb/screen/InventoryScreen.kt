package ru.MeatGames.roguelike.tomb.screen

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import ru.MeatGames.roguelike.tomb.Assets
import ru.MeatGames.roguelike.tomb.GameController
import ru.MeatGames.roguelike.tomb.InventoryFilterType
import ru.MeatGames.roguelike.tomb.R
import ru.MeatGames.roguelike.tomb.model.Item
import ru.MeatGames.roguelike.tomb.util.ScreenHelper
import ru.MeatGames.roguelike.tomb.util.UnitConverter
import ru.MeatGames.roguelike.tomb.view.TextButton
import java.util.*

class InventoryScreen(context: Context,
                      filter: InventoryFilterType?) : BasicScreen(context) {

    override val TAG: String = "Inventory Screen"

    private val mEquippedItemBackgroundPaint = Paint()
    private val mMainTextPaint: Paint
    private val mSecondaryTextPaint: Paint

    private var sx: Int = 0 //ACTION_DOWN
    private var sy: Int = 0
    private var lx: Int = 0 //ACTION_UP,ACTION_MOVE
    private var ly: Int = 0

    private val mMaxItemsOnScreen: Int
    private var mItemsOnScreen: Int = 0

    private val mScrollDeadZone = UnitConverter.convertDpToPixels(9F, context)
    private var mCurrentScroll: Int = 0
    private var mMaxScroll: Int = 0
    private var mSavedScroll = 0
    private var scroll = false
    private var scrollPermission = true
    private var tap = false

    // vars for drawing item mItemList
    private var mItemList: LinkedList<Item?> = LinkedList()
    private val mItemListPadding: Float
    private val mSpaceBetweenItemPanels: Float
    private val mItemPanelHeight: Float
    private val mItemPanelCombinedHeight: Int // ItemPanelHeight + SpaceBetweenItemPanels
    private val mTextVerticalPadding = UnitConverter.convertDpToPixels(4F, context)
    private val mTextHorizontalPadding = UnitConverter.convertDpToPixels(10F, context)

    // regions for touch input and drawing specific screen parts
    private val mScreenRect: Rect
    private val mItemListRect: Rect

    private val mBackButton: TextButton
    private val mLeftSoftButton: TextButton // needs proper name

    // vars for touch input and drawing filter buttons
    private val mFilterPanelBorder: Float
    private val mFilterButtonsWidth: Float
    private val mFilterStates: BooleanArray = BooleanArray(5, { true })
    private val mFilterIcons: List<Bitmap>

    init {
        mEquippedItemBackgroundPaint.color = resources.getColor(R.color.framegrn)

        mMainTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mMainTextPaint.textSize = 24f

        mSecondaryTextPaint = ScreenHelper.getDefaultTextPaint(context)
        mSecondaryTextPaint.textAlign = Paint.Align.LEFT

        mItemListPadding = UnitConverter.convertDpToPixels(16F, context)

        mFilterPanelBorder = mScreenHeight * 0.075F
        mFilterButtonsWidth = mScreenWidth * 0.2F

        val assetHelper = Assets.mAssetHelper
        mFilterIcons = listOf(Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("weapons_icon_outline"), 30, 30, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("weapons_icon_filling"), 30, 30, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("shield_icon_outline"), 32, 36, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("shield_icon_filling"), 32, 36, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("armor_icon_outline"), 38, 34, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("armor_icon_filling"), 38, 34, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("gear_icon_outline"), 32, 28, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("gear_icon_filling"), 32, 28, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("consumables_icon_outline"), 24, 34, false),
                Bitmap.createScaledBitmap(assetHelper.getBitmapFromAsset("consumables_icon_filling"), 24, 34, false))

        mLeftSoftButton = TextButton(context, resources.getString(R.string.gear_label))
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

        mSpaceBetweenItemPanels = UnitConverter.convertDpToPixels(2F, context)

        mItemPanelHeight = UnitConverter.convertDpToPixels(40F, context)
        mItemPanelCombinedHeight = (mItemPanelHeight + mSpaceBetweenItemPanels).toInt()

        mScreenRect = Rect(0, 0, mScreenWidth, mScreenHeight)
        mItemListRect = Rect(mItemListPadding.toInt(),
                (mFilterPanelBorder + mItemListPadding).toInt(),
                (mScreenWidth - mItemListPadding).toInt(),
                (mScreenHeight * 0.9F - mItemListPadding).toInt())

        mMaxItemsOnScreen = if (mItemListRect.height() % mItemPanelCombinedHeight == 0) {
            mItemListRect.height() / mItemPanelCombinedHeight
        } else {
            mItemListRect.height() / mItemPanelCombinedHeight + 1
        }

        filter?.let { setFilters(filter) }
        showItemList()
    }

    private fun setFilters(isWeaponsAllowed: Boolean, isShieldsAllowed: Boolean,
                           isArmorAllowed: Boolean, isGearAllowed: Boolean,
                           isConsumablesAllowed: Boolean) {
        mFilterStates[0] = isWeaponsAllowed
        mFilterStates[1] = isShieldsAllowed
        mFilterStates[2] = isArmorAllowed
        mFilterStates[3] = isGearAllowed
        mFilterStates[4] = isConsumablesAllowed
    }

    private fun setFilters(filter: InventoryFilterType) {
        when(filter) {
            InventoryFilterType.WEAPONS -> setFilters(true, false, false, false, false)
            InventoryFilterType.SHIELDS -> setFilters(false, true, false, false, false)
            InventoryFilterType.ARMOR -> setFilters(false, false, true, false, false)
        }
    }

    private fun isAllowed(item: Item): Boolean =
            mFilterStates[0] && item.isWeapon
                    || mFilterStates[1] && item.isShield
                    || mFilterStates[2] && item.isArmor
                    || mFilterStates[3] && item.isGear
                    || mFilterStates[4] && item.isConsumable

    // populating inventory mItemList according to selected flags
    fun populateItemList() {
        mItemList = LinkedList()

        GameController.mHero.mInventory?.let {
            it
                    .filter { isAllowed(it) }
                    .forEach { mItemList.add(it) }
        }

        mItemsOnScreen = mItemList.size
        mMaxScroll = if (mItemsOnScreen > mMaxItemsOnScreen) {
            -(mItemList.size * mItemPanelCombinedHeight - mItemListRect.height())
        } else {
            0
        }
    }

    private fun drawFlags(canvas: Canvas) {
        for (i in 0..4) {
            val iconBitmap = if (!mFilterStates[i]) mFilterIcons[i * 2] else mFilterIcons[i * 2 + 1]
            canvas.drawBitmap(iconBitmap,
                    mFilterButtonsWidth * i + (mFilterButtonsWidth - iconBitmap.width) / 2,
                    (mFilterPanelBorder - iconBitmap.height) / 2,
                    null)
        }
    }

    internal fun drawList(canvas: Canvas) {
        canvas.clipRect(mItemListRect, Region.Op.REPLACE)

        if (mItemList.size > 0) {
            val u = -(mCurrentScroll + mSavedScroll) / mItemPanelCombinedHeight
            val offset = -(mCurrentScroll + mSavedScroll) % mItemPanelCombinedHeight
            var q = 0
            mItemList.forEach {
                if (q >= u) {
                    val top = mItemListRect.top + (q - u) * (mSpaceBetweenItemPanels + mItemPanelHeight) - offset
                    val bottom = top + mItemPanelHeight
                    val itemPanelBackground = if (!it!!.isConsumable && GameController.mHero.isEquipped(it)) {
                        mEquippedItemBackgroundPaint
                    } else {
                        mBackgroundPaint
                    }
                    canvas.drawRect(mItemListRect.left.toFloat(), top, mItemListRect.right.toFloat(), bottom, itemPanelBackground)
                    canvas.drawBitmap(it.image, mItemListRect.left + (mItemPanelHeight - it.image.width) * 0.5F, top + (mItemPanelHeight - it.image.height) / 2, null)
                    canvas.drawText(it.mTitle, mItemListRect.left + mItemPanelHeight + mTextHorizontalPadding, top + mItemPanelHeight * 0.5F + mTextVerticalPadding, mSecondaryTextPaint)
                }
                q++
            }
        } else {
            canvas.drawText(context.getString(R.string.inventory_is_empty_label), mScreenWidth * 0.5F, mScreenHeight * 0.125F, mMainTextPaint)
        }

        mLeftSoftButton.mLabel = context.getString(R.string.gear_label)
        canvas.clipRect(mScreenRect, Region.Op.REPLACE)
    }

    override fun drawScreen(canvas: Canvas?) {
        drawBackground(canvas!!)
        drawFlags(canvas)
        drawList(canvas)
        mLeftSoftButton.draw(canvas)
        mBackButton.draw(canvas)
        mSecondaryTextPaint.textAlign = Paint.Align.LEFT
        postInvalidate()
    }

    private fun showItemList(isWeaponsAllowed: Boolean = mFilterStates[0],
                             isShieldsAllowed: Boolean = mFilterStates[1],
                             isArmorAllowed: Boolean = mFilterStates[2],
                             isGearAllowed: Boolean = mFilterStates[3],
                             isConsumablesAllowed: Boolean = mFilterStates[4]) {
        scrollPermission = true

        mCurrentScroll = 0
        mSavedScroll = 0

        setFilters(isWeaponsAllowed, isShieldsAllowed, isArmorAllowed, isGearAllowed, isConsumablesAllowed)
        populateItemList()
    }

    private fun findItem(id: Int): Item? {
        return if (mItemList.size > id) {
            mItemList[id]
        } else {
            null
        }
    }

    fun onTouchInv(sx: Int, sy: Int) {
        if (sy < mFilterPanelBorder) { // filter buttons panel
            mFilterStates[sx / mFilterButtonsWidth.toInt()] = !mFilterStates[sx / (mScreenWidth / 5)]
            showItemList()
        }

        if (mLeftSoftButton.isPressed(sx, sy)) {
            GameController.changeScreen(Screens.GEAR_SCREEN)
        }

        if (mBackButton.isPressed(sx, sy)) {
            GameController.changeScreen(Screens.GAME_SCREEN)
        }

        if (mItemListRect.contains(sx, sy)) {
            val possibleItem = (sy - mItemListRect.top - mSavedScroll) / mItemPanelCombinedHeight
            findItem(possibleItem)?.let {
                GameController.selectedItem = it
                GameController.changeScreen(Screens.DETAILED_ITEM_SCREEN)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sx = event.x.toInt()
                lx = sx
                sy = event.y.toInt()
                ly = sy
                tap = true
            }
            MotionEvent.ACTION_MOVE -> {
                lx = event.x.toInt()
                ly = event.y.toInt()
                if (Math.abs(lx - sx) > mScrollDeadZone || Math.abs(ly - sy) > mScrollDeadZone) {
                    if (tap)
                        tap = false
                    if (scrollPermission && !scroll) {
                        scroll = true
                        mCurrentScroll = 0
                        sx = lx
                        sy = ly
                    }
                }
                if (scroll) {
                    if (mMaxScroll != 0) {
                        if (ly - sy + mSavedScroll < mMaxScroll) {
                            mCurrentScroll = mMaxScroll - mSavedScroll
                        } else if (ly - sy + mSavedScroll > 0) {
                            mCurrentScroll = -mSavedScroll
                        } else {
                            mCurrentScroll = ly - sy
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!scroll) {
                    if (tap) {
                        onTouchInv(sx, sy)
                    }
                } else {
                    mSavedScroll += mCurrentScroll
                    mCurrentScroll = 0
                }
                tap = false
                scroll = tap
            }
        }
        return true
    }

}