package ru.MeatGames.roguelike.tomb

import android.graphics.Bitmap
import android.os.Vibrator
import org.xmlpull.v1.XmlPullParser
import ru.MeatGames.roguelike.tomb.db.ItemDB
import ru.MeatGames.roguelike.tomb.db.MobDB
import ru.MeatGames.roguelike.tomb.db.StatsDB
import ru.MeatGames.roguelike.tomb.db.TileDB
import ru.MeatGames.roguelike.tomb.model.HeroClass
import ru.MeatGames.roguelike.tomb.model.MapClass
import ru.MeatGames.roguelike.tomb.screen.GameScreen
import ru.MeatGames.roguelike.tomb.screen.MainMenu
import ru.MeatGames.roguelike.tomb.util.AssetHelper
import ru.MeatGames.roguelike.tomb.util.MapHelper

object Global {

    lateinit var game: Game
    var mapWidth: Int = 96
    var mapHeight: Int = 96

    var hero: HeroClass? = null
    var map: Array<Array<MapClass>>? = null

    lateinit var mapview: GameScreen
    lateinit var mmview: MainMenu

    // temporary most of the tiles is null, use carefully
    lateinit var tiles: Array<TileDB?> // 0 element is opaque
    lateinit var objects: Array<TileDB>
    lateinit var itemDB: Array<ItemDB>
    lateinit var mobDB: Array<MobDB>
    lateinit var stats: Array<StatsDB>
    lateinit var walls: Array<Bitmap>
    lateinit var heroSprites: Array<Bitmap>

    lateinit var mAssetHelper: AssetHelper

    lateinit var mVibrator: Vibrator

    var mIsInitialDataCreated: Boolean = false

    val maxTiles = 13
    val maxObjects = 16
    val maxItems = 17
    val maxStats = 35

    private val mOriginalTileSize = 24

    inline fun <reified INNER> array2d(sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int)->INNER): Array<Array<INNER>>
            = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

    @JvmStatic
    fun vibrate() {
        mVibrator.vibrate(30)
    }

    @JvmStatic
    fun initGame(game: Game) {
        this.game = game
        MapHelper.mapWidth = mapWidth
        MapHelper.mapHeight = mapHeight
    }

    @JvmStatic
    fun initInitialData() {
        if (!mIsInitialDataCreated) {
            hero = HeroClass()
            mapview = GameScreen(game)

            map = array2d(mapWidth, mapHeight, { MapClass() } )

            loadAssets()

            mIsInitialDataCreated = true
        }
    }

    fun loadAssets() {
        loadStats()
        loadTiles()
        loadObjects()
        loadItems()
        loadMobs()

        // loadAssets images
        var temp = mAssetHelper.getBitmapFromAsset("character_animation_sheet")

        heroSprites = Array(4) { i -> Bitmap.createBitmap(temp,
                i * mOriginalTileSize,
                0,
                mOriginalTileSize,
                mOriginalTileSize)}

        game.bag = mAssetHelper.getBitmapFromAsset("bag")
        game.mCharacterIcon = mAssetHelper.getBitmapFromAsset("character_icon")
        game.mInventoryIcon = mAssetHelper.getBitmapFromAsset("inventory_icon")
        game.mBackIcon = mAssetHelper.getBitmapFromAsset("back_icon")
        game.d = mAssetHelper.getBitmapFromAsset("ery")
        game.mSkipTurnIcon = mAssetHelper.getBitmapFromAsset("skip_turn_icon")

        temp = mAssetHelper.getBitmapFromAsset("floor_tileset")
        for (i in 0..maxTiles - 1) {
            tiles[i]?.img = Bitmap.createBitmap(temp,
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
        for (x in 0..game.maxMobs - 1) {
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
        walls = Array<Bitmap>(16) {i -> Bitmap.createBitmap(temp, i % 4 * mOriginalTileSize, i / 4 * mOriginalTileSize, mOriginalTileSize, mOriginalTileSize)}
    }

    private fun loadStats() {
        val parser = game.resources.getXml(R.xml.stats)
        stats = Array(maxStats) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "stat") {
                    parser.next()
                    break
                }
                parser.next()
            }
            val statTitle = parser.getAttributeValue(0)
            val isSingle = parser.getAttributeValue(1) == "t"
            val isMaximum = parser.getAttributeValue(2) == "t"
            StatsDB(statTitle, isSingle, isMaximum)
        }
    }

    private fun loadTiles() {
        val parser = game.resources.getXml(R.xml.tiles)
        tiles = Array(maxTiles) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "tile") {
                    parser.next()
                    break
                }
                parser.next()
            }
            val isPassable = parser.getAttributeValue(0) == "t"
            val isTransparent = parser.getAttributeValue(1) == "t"
            val isUsable = parser.getAttributeValue(2) == "t"
            TileDB(isPassable, isTransparent, isUsable)
        }
    }

    private fun loadObjects() {
        val parser = game.resources.getXml(R.xml.objects)
        objects = Array(maxObjects) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "object") {
                    parser.next()
                    break
                }
                parser.next()
            }
            val isPassable = parser.getAttributeValue(0) == "t"
            val isTransparent = parser.getAttributeValue(1) == "t"
            val isUsable = parser.getAttributeValue(2) == "t"
            TileDB(isPassable, isTransparent, isUsable)
        }
    }

    private fun loadItems() {
        val parser = game.resources.getXml(R.xml.items)
        itemDB = Array(maxItems) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "items") {
                    parser.next()
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

            ItemDB(type, title, titleEnding, value1, value2, value3, property)
        }
    }

    private fun loadMobs() {
        val parser = game.resources.getXml(R.xml.mobs   )
        mobDB = Array(Global.game.maxMobs) {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "mob") {
                    parser.next()
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
            MobDB(name, health, attack, defense, armor, speed, damage)
        }
    }

    @JvmStatic
    fun newHero() {
        hero = HeroClass()
    }

    @JvmStatic
    fun getHeroSprite(frame: Int): Bitmap = heroSprites[frame]

}