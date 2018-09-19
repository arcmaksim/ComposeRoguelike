package ru.meatgames.tomb

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import org.xmlpull.v1.XmlPullParser
import ru.meatgames.tomb.db.*
import ru.meatgames.tomb.util.ScreenHelper
import java.io.IOException
import java.io.InputStream

object Assets {

    private lateinit var mMainActivity: MainActivity

    lateinit var tiles: Array<TileDB>
    lateinit var objects: Array<ObjectDB> // 0 element is opaque
    lateinit var itemDB: Array<ItemDB>
    lateinit var mobDB: Array<MobDB>
    lateinit var stats: Array<StatsDB>
    private lateinit var walls: Array<Bitmap>
    private lateinit var heroSprites: Array<Bitmap>

    lateinit var mImageRects: Array<Rect>

    lateinit var mCharacterIcon: Bitmap
    lateinit var mInventoryIcon: Bitmap
    lateinit var mSkipTurnIcon: Bitmap
    lateinit var lastAttack: Bitmap
    lateinit var bag: Bitmap
    lateinit var mFilterIcons: Array<Bitmap>

    private lateinit var mFloorTileset: Bitmap
    private lateinit var mObjectTileset: Bitmap
    private lateinit var mItemsSheet: Bitmap
    // creatures have 2 frames
    private lateinit var mCreaturesSheetDefault: Bitmap
    private lateinit var mCreaturesSheetAlternative: Bitmap

    val maxTiles = 12
    val maxObjects = 17
    val maxItems = 17
    val maxStats = 35

    val mOriginalTileSize: Int = 24
    var mScaleAmount: Int = 0
    var mActualTileSize: Int = 0

    @JvmStatic
    fun init(game: MainActivity) {
        mMainActivity = game

        init()

        calculateTileSize()
        loadAssets()
    }

    private fun init() {
        // empty
    }

    private fun calculateTileSize() {
        val screenSize = ScreenHelper.getScreenSize(mMainActivity.windowManager)
        mScaleAmount = (screenSize.x / (mOriginalTileSize * 10f)).toInt()
        mActualTileSize = mOriginalTileSize * mScaleAmount
    }

    fun loadAssets() {
        loadStats()
        loadTiles()
        loadObjects()
        loadItems()
        loadMobs()

        mImageRects = Array(100) { i ->
            Rect(i % 5 * mOriginalTileSize,
                    i / 5 * mOriginalTileSize,
                    i % 5 * mOriginalTileSize + mOriginalTileSize,
                    i / 5 * mOriginalTileSize + mOriginalTileSize)
        }

        var temp = getBitmapFromAsset("character_animation_sheet")
        heroSprites = Array(4) { i ->
            Bitmap.createBitmap(temp,
                    i * mOriginalTileSize,
                    0,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        bag = getBitmapFromAsset("bag")
        mCharacterIcon = getBitmapFromAsset("character_icon")
        mInventoryIcon = getBitmapFromAsset("inventory_icon")
        mSkipTurnIcon = getBitmapFromAsset("skip_turn_icon")

        mFloorTileset = getBitmapFromAsset("floor_tileset")
        mObjectTileset = getBitmapFromAsset("objects_tileset")
        mItemsSheet = getBitmapFromAsset("items_sheet")
        mCreaturesSheetDefault = getBitmapFromAsset("creatures_sheet")
        mCreaturesSheetAlternative = getBitmapFromAsset("creatures_sheet_alt")

        temp = getBitmapFromAsset("walls_tileset")
        walls = Array(16) { i -> Bitmap.createBitmap(temp, i % 4 * mOriginalTileSize, i / 4 * mOriginalTileSize, mOriginalTileSize, mOriginalTileSize) }

        mFilterIcons = arrayOf(Bitmap.createScaledBitmap(getBitmapFromAsset("weapons_icon_outline"), 30, 30, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("weapons_icon_filling"), 30, 30, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("shield_icon_outline"), 32, 36, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("shield_icon_filling"), 32, 36, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("armor_icon_outline"), 38, 34, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("armor_icon_filling"), 38, 34, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("gear_icon_outline"), 32, 28, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("gear_icon_filling"), 32, 28, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("consumables_icon_outline"), 24, 34, false),
                Bitmap.createScaledBitmap(getBitmapFromAsset("consumables_icon_filling"), 24, 34, false))
    }

    private fun loadStats() {
        val parser = mMainActivity.resources.getXml(R.xml.stats)
        stats = Array(maxStats) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "stat") {
                    break
                }
                parser.next()
            }
            val statTitle = parser.getAttributeValue(1)
            val isSingle = parser.getAttributeValue(2) == "t"
            val isMaximum = parser.getAttributeValue(0) == "t"
            parser.next()
            StatsDB(statTitle, isSingle, isMaximum)
        }
    }

    private fun loadTiles() {
        val parser = mMainActivity.resources.getXml(R.xml.tiles)
        tiles = Array(maxTiles) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "tile") {
                    break
                }
                parser.next()
            }
            val isPassable = parser.getAttributeValue(0) == "t"
            val isTransparent = parser.getAttributeValue(1) == "t"
            val isUsable = parser.getAttributeValue(2) == "t"
            parser.next()
            TileDB(isPassable, isTransparent, isUsable)
        }
    }

    private fun loadObjects() {
        val parser = mMainActivity.resources.getXml(R.xml.objects)
        objects = Array(maxObjects) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "object") {
                    break
                }
                parser.next()
            }
            val isPassable = parser.getAttributeValue(1) == "t"
            val isTransparent = parser.getAttributeValue(2) == "t"
            val isUsable = parser.getAttributeValue(3) == "t"
            val isWall = parser.getAttributeValue(0) == "t"
            parser.next()
            ObjectDB(isPassable, isTransparent, isUsable, isWall)
        }
    }

    private fun loadItems() {
        val parser = mMainActivity.resources.getXml(R.xml.items)
        itemDB = Array(maxItems) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG &&
                        (parser.name == "weapon"
                                || parser.name == "shield"
                                || parser.name == "armor"
                                || parser.name == "item")) {
                    break
                }
                parser.next()
            }

            var type = 1
            var title = ""
            var titleEnding = ""
            var value1 = 0
            var value2 = 0
            var value3 = 0
            var property = false

            when (parser.name) {
                "weapon" -> {
                    type = 1
                    title = parser.getAttributeValue(5)
                    titleEnding = parser.getAttributeValue(2)
                    value1 = Integer.parseInt(parser.getAttributeValue(0))
                    value2 = Integer.parseInt(parser.getAttributeValue(4))
                    value3 = Integer.parseInt(parser.getAttributeValue(3))
                    property = parser.getAttributeValue(1) == "t"
                }
                "shield" -> {
                    type = 2
                    title = parser.getAttributeValue(3)
                    titleEnding = parser.getAttributeValue(2)
                    value1 = Integer.parseInt(parser.getAttributeValue(1))
                    value2 = Integer.parseInt(parser.getAttributeValue(0))
                }
                "armor" -> {
                    type = 3
                    title = parser.getAttributeValue(3)
                    titleEnding = parser.getAttributeValue(2)
                    value1 = Integer.parseInt(parser.getAttributeValue(1))
                    value2 = Integer.parseInt(parser.getAttributeValue(0))
                }
                "item" -> {
                    type = 5
                    title = parser.getAttributeValue(2)
                    titleEnding = parser.getAttributeValue(0)
                    value1 = Integer.parseInt(parser.getAttributeValue(1))
                    value2 = Integer.parseInt(parser.getAttributeValue(3))
                }
            }

            parser.next()
            ItemDB(type, title, titleEnding, value1, value2, value3, property)
        }
    }

    private fun loadMobs() {
        val parser = mMainActivity.resources.getXml(R.xml.mobs)
        mobDB = Array(GameController.maxMobs) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "mob") {
                    break
                }
                parser.next()
            }

            val name = parser.getAttributeValue(5)
            val health = Integer.parseInt(parser.getAttributeValue(4))
            val attack = Integer.parseInt(parser.getAttributeValue(1))
            val defense = Integer.parseInt(parser.getAttributeValue(3))
            val armor = Integer.parseInt(parser.getAttributeValue(0))
            val speed = Integer.parseInt(parser.getAttributeValue(6))
            val damage = Integer.parseInt(parser.getAttributeValue(2))
            parser.next()
            MobDB(name, health, attack, defense, armor, speed, damage)
        }
    }

    private fun getBitmapFromAsset(bitmapName: String): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = mMainActivity.assets.open("images/$bitmapName.png")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BitmapFactory.decodeStream(inputStream)
    }

    @JvmStatic
    fun getHeroSprite(frame: Int): Bitmap = heroSprites[frame]

    fun getFloorImage() = mFloorTileset

    fun getObjectImage() = mObjectTileset

    fun getWallImage(wallId: Int) = walls[wallId]

    fun getItemImage() = mItemsSheet

    fun getCreatureImage(frame: Int): Bitmap {
        return if (frame == 0) mCreaturesSheetDefault else mCreaturesSheetAlternative
    }

    fun getAssetRect(id: Int) = mImageRects[id]

    fun getInventoryFilterIcon(id: Int) = mFilterIcons[id]

}