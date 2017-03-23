package ru.MeatGames.roguelike.tomb

import android.graphics.Bitmap
import org.xmlpull.v1.XmlPullParser
import ru.MeatGames.roguelike.tomb.db.*
import ru.MeatGames.roguelike.tomb.util.AssetHelper
import ru.MeatGames.roguelike.tomb.util.ScreenHelper

object Assets {

    private lateinit var mMainActivity: MainActivity

    lateinit var tiles: Array<TileDB>
    lateinit var objects: Array<ObjectDB> // 0 element is opaque
    lateinit var itemDB: Array<ItemDB>
    lateinit var mobDB: Array<MobDB>
    lateinit var stats: Array<StatsDB>
    lateinit var walls: Array<Bitmap>
    lateinit var heroSprites: Array<Bitmap>

    lateinit var mCharacterIcon: Bitmap
    lateinit var mInventoryIcon: Bitmap
    lateinit var mBackIcon: Bitmap
    lateinit var d: Bitmap
    lateinit var mSkipTurnIcon: Bitmap
    lateinit var lastAttack: Bitmap
    lateinit var bag: Bitmap

    lateinit var mAssetHelper: AssetHelper

    val maxTiles = 12
    val maxObjects = 17
    val maxItems = 17
    val maxStats = 35

    val mOriginalTileSize = 24
    var mScaleAmount: Float = 0.toFloat()
    var mActualTileSize: Float = 0.toFloat()

    @JvmStatic
    fun init(game: MainActivity) {
        mMainActivity = game

        init()

        calculateTileSize()
        loadAssets()
    }

    private fun init() {
        mAssetHelper = AssetHelper(mMainActivity.assets)
    }

    private fun calculateTileSize() {
        val screenSize = ScreenHelper.getScreenSize(mMainActivity.windowManager)
        mScaleAmount = screenSize.x / (mOriginalTileSize * 10f)
        mActualTileSize = mOriginalTileSize * mScaleAmount
    }

    fun loadAssets() {
        loadStats()
        loadTiles()
        loadObjects()
        loadItems()
        loadMobs()

        // loadAssets images
        var temp = mAssetHelper.getBitmapFromAsset("character_animation_sheet")

        heroSprites = Array(4) { i ->
            Bitmap.createBitmap(temp,
                    i * mOriginalTileSize,
                    0,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        bag = mAssetHelper.getBitmapFromAsset("bag")
        mCharacterIcon = mAssetHelper.getBitmapFromAsset("character_icon")
        mInventoryIcon = mAssetHelper.getBitmapFromAsset("inventory_icon")
        mBackIcon = mAssetHelper.getBitmapFromAsset("back_icon")
        d = mAssetHelper.getBitmapFromAsset("ery")
        mSkipTurnIcon = mAssetHelper.getBitmapFromAsset("skip_turn_icon")

        temp = mAssetHelper.getBitmapFromAsset("floor_tileset")
        for (i in 0..maxTiles - 1) {
            tiles[i].img = Bitmap.createBitmap(temp,
                    i % 5 * mOriginalTileSize,
                    i / 5 * mOriginalTileSize,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        temp = mAssetHelper.getBitmapFromAsset("objects_tileset")
        for (i in 0..maxObjects - 1) {
            objects[i].img = Bitmap.createBitmap(temp,
                    i % 5 * mOriginalTileSize,
                    i / 5 * mOriginalTileSize,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        temp = mAssetHelper.getBitmapFromAsset("items_sheet")
        for (i in 0..maxItems - 1) {
            itemDB[i].img = Bitmap.createBitmap(temp,
                    i % 5 * mOriginalTileSize,
                    i / 5 * mOriginalTileSize,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        temp = mAssetHelper.getBitmapFromAsset("mobs_sheet")
        for (x in 0..GameController.maxMobs - 1) {
            mobDB[x].img[0] = Bitmap.createBitmap(temp,
                    x * mOriginalTileSize,
                    0,
                    mOriginalTileSize,
                    mOriginalTileSize)
            mobDB[x].img[1] = Bitmap.createBitmap(temp,
                    x * mOriginalTileSize,
                    mOriginalTileSize,
                    mOriginalTileSize,
                    mOriginalTileSize)
        }

        temp = mAssetHelper.getBitmapFromAsset("walls_tileset")
        walls = Array<Bitmap>(16) { i -> Bitmap.createBitmap(temp, i % 4 * mOriginalTileSize, i / 4 * mOriginalTileSize, mOriginalTileSize, mOriginalTileSize) }
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
            val statTitle = parser.getAttributeValue(0)
            val isSingle = parser.getAttributeValue(1) == "t"
            val isMaximum = parser.getAttributeValue(2) == "t"
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
            val isPassable = parser.getAttributeValue(0) == "t"
            val isTransparent = parser.getAttributeValue(1) == "t"
            val isUsable = parser.getAttributeValue(2) == "t"
            val isWall = parser.getAttributeValue(3) == "t"
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
            val title = parser.getAttributeValue(0)
            val titleEnding = parser.getAttributeValue(1)
            val value1 = Integer.parseInt(parser.getAttributeValue(2))
            val value2 = Integer.parseInt(parser.getAttributeValue(3))
            var value3 = 0
            var property = false

            when (parser.name) {
                "weapon" -> {
                    type = 1
                    value3 = Integer.parseInt(parser.getAttributeValue(4))
                    property = parser.getAttributeValue(5) == "t"
                }
                "shield" -> type = 2
                "armor" -> type = 3
                "item" -> type = 5
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

            val name = parser.getAttributeValue(0)
            val health = Integer.parseInt(parser.getAttributeValue(1))
            val attack = Integer.parseInt(parser.getAttributeValue(2))
            val defense = Integer.parseInt(parser.getAttributeValue(3))
            val armor = Integer.parseInt(parser.getAttributeValue(4))
            val speed = Integer.parseInt(parser.getAttributeValue(5))
            val damage = Integer.parseInt(parser.getAttributeValue(6))
            parser.next()
            MobDB(name, health, attack, defense, armor, speed, damage)
        }
    }

    @JvmStatic
    fun getHeroSprite(frame: Int): Bitmap = heroSprites[frame]

}