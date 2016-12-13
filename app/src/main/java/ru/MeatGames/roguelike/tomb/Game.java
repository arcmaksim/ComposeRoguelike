package ru.MeatGames.roguelike.tomb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;

import java.util.Random;

import ru.MeatGames.roguelike.tomb.db.ItemDB;
import ru.MeatGames.roguelike.tomb.db.MobDB;
import ru.MeatGames.roguelike.tomb.db.StatsDB;
import ru.MeatGames.roguelike.tomb.db.TileDB;
import ru.MeatGames.roguelike.tomb.model.HeroClass;
import ru.MeatGames.roguelike.tomb.model.Item;
import ru.MeatGames.roguelike.tomb.model.MapClass;
import ru.MeatGames.roguelike.tomb.screen.CharacterScreen;
import ru.MeatGames.roguelike.tomb.screen.DetailedItemScreen;
import ru.MeatGames.roguelike.tomb.screen.GameScreen;
import ru.MeatGames.roguelike.tomb.screen.GearScreen;
import ru.MeatGames.roguelike.tomb.screen.InventoryScreen;
import ru.MeatGames.roguelike.tomb.screen.MainMenu;
import ru.MeatGames.roguelike.tomb.screen.MapScreen;
import ru.MeatGames.roguelike.tomb.screen.Screens;
import ru.MeatGames.roguelike.tomb.util.AssetHelper;
import ru.MeatGames.roguelike.tomb.util.ScreenHelper;

public class Game extends Activity {

    public static Vibrator v;
    public static int curLvls = 0;
    public final int mw = 96; //64
    public final int mh = 96; //64
    public final int mTileSize = 24; // in pixels
    private float mScaleAmount;
    private float mActualTileSize;
    public final int maxTiles = 100;
    public final int maxItems = 17;
    public final int maxStats = 35;
    public final int maxMobs = 6;
    public final int defValue = 99;
    public final int maxLvl = 3;
    public int turnCount = 0;
    public boolean turn = true;
    public boolean move = true;
    public boolean tap;
    public Bitmap mCharacterIcon;
    public Bitmap mInventoryIcon;
    public Bitmap mBackIcon;
    public Bitmap d;
    public Bitmap mSkipTurnIcon;
    public Random mRandom;
    public boolean lines = false;
    public int scr = 0;
    public Bitmap lastAttack;
    public Bitmap bag;
    public MobList firstMob;
    public int[][] zone;
    // TODO: temporal solution
    public Item selectedItem;
    private Screens lastScreen;

    public static Bitmap getHeroImg(int n) {
        return Global.INSTANCE.getHero().img[n];
    }

    public static int getFloor(int x, int y) {
        return Global.INSTANCE.getMap()[x][y].mFloorID;
    }

    public static int getObject(int x, int y) {
        return Global.INSTANCE.getMap()[x][y].mObjectID;
    }

    protected void onCreate(Bundle w) {
        super.onCreate(w);

        Global.INSTANCE.setGame(this);
        mRandom = new Random();

        zone = new int[11][11];

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Point screenSize = ScreenHelper.getScreenSize(getWindowManager());
        mScaleAmount = screenSize.x / (mTileSize * 10f);
        mActualTileSize = mTileSize * mScaleAmount;

        Global.INSTANCE.setMap(new MapClass[mw][mh]);
        for (int x = 0; x < mw; x++)
            for (int y = 0; y < mh; y++)
                Global.INSTANCE.getMap()[x][y] = new MapClass();

        Global.INSTANCE.setHero(new HeroClass());
        Global.INSTANCE.setMmview(new MainMenu(this));
        Global.INSTANCE.setMapview(new GameScreen(this));

        Global.INSTANCE.setMAssetHelper(new AssetHelper(getAssets()));
        loading();

        newGame();
        mainBody();
    }

    public float getScaleAmount() {
        return mScaleAmount;
    }

    public float getActualTileSize() {
        return mActualTileSize;
    }

    public void onBackPressed() {
        if (scr == 0 && !Global.INSTANCE.getMapview().getMDrawProgressBar()) {
            Global.INSTANCE.getMapview().setMDrawExitDialog(!Global.INSTANCE.getMapview().getMDrawExitDialog());
        }
    }

    public void exitGame() {
        finish();
    }

    public void newGame() {
        Global.INSTANCE.getHero().newHero();
        curLvls = 0;
        generateNewMap();
        Global.INSTANCE.getMapview().clearLog();
        changeScreen(Screens.MAIN_MENU);
    }

    public void changeScreen(Screens screen) {
        View view = null;
        switch (screen) {
            case GAME_SCREEN:
                view = Global.INSTANCE.getMapview();
                break;
            case INVENTORY_SCREEN:
                lastScreen = Screens.INVENTORY_SCREEN;
                view = new InventoryScreen(this, null);
                break;
            case CHARACTER_SCREEN:
                view = new CharacterScreen(this);
                break;
            case MAP_SCREEN:
                view = new MapScreen(this);
                break;
            case MAIN_MENU:
                view = Global.INSTANCE.getMmview();
                break;
            case GEAR_SCREEN:
                lastScreen = Screens.GEAR_SCREEN;
                view = new GearScreen(this);
                break;
            case DETAILED_ITEM_SCREEN:
                view = new DetailedItemScreen(this, selectedItem);
                break;
        }
        setContentView(view);
        view.requestFocus();
    }

    public void changeToLastScreen() {
        changeScreen(lastScreen);
    }

    public void showInventoryWithFilters(InventoryFilterType filter) {
        InventoryScreen inventoryScreen = new InventoryScreen(this, filter);
        setContentView(inventoryScreen);
        inventoryScreen.requestFocus();
    }

    public void generateNewMap() {
        MapGenerator mapGenerator = new MapGenerator();
        mapGenerator.generateMap();
    }

    public Item createItem() {
        Item item = new Item(mRandom.nextInt(13));
        /*item.id = mRandom.nextInt(13);
        item.title = Global.itemDB[item.id].title;
        item.mTitleEnding = Global.itemDB[item.id].mTitleEnding;
        item.mType = Global.itemDB[item.id].mType;
        item.mValue1 = Global.itemDB[item.id].mValue1;
        item.mValue2 = Global.itemDB[item.id].mValue2;
        item.mValue3 = Global.itemDB[item.id].mValue3;
        item.mProperty = Global.itemDB[item.id].mProperty;*/
        return item;
    }

    public Item createItem(int t) {
        Item item = new Item(t);
        /*item.id = t;
        item.title = Global.itemDB[item.id].title;
        item.mTitleEnding = Global.itemDB[item.id].mTitleEnding;
        item.mType = Global.itemDB[item.id].mType;
        item.mValue1 = Global.itemDB[item.id].mValue1;
        item.mValue2 = Global.itemDB[item.id].mValue2;
        item.mValue3 = Global.itemDB[item.id].mValue3;
        item.mProperty = Global.itemDB[item.id].mProperty;*/
        return item;
    }

    public void createMob(int x, int y) {
        MobList temp = new MobList(mRandom.nextInt(4));
        temp.x = x;
        temp.y = y;
        Global.INSTANCE.getMap()[x][y].addMob(temp);
        addInQueue(temp);
    }

    public void createMob(int x, int y, int t) {
        MobList temp = new MobList(t);
        temp.x = x;
        temp.y = y;
        Global.INSTANCE.getMap()[x][y].addMob(temp);
        addInQueue(temp);
    }

    public void addInQueue(MobList mob) {
        mob.turnCount = turnCount + mob.mob.getMSpeed();
        if (firstMob == null) {
            firstMob = mob;
        } else {
            MobList cur;
            boolean b = false;
            for (cur = firstMob; cur.turnCount <= mob.turnCount; cur = cur.next)
                if (cur.next == null) {
                    b = true;
                    break;
                }
            if (b) {
                cur.next = mob;
                cur.next.next = null;
            } else {
                MobList temp;
                if (cur == firstMob) {
                    temp = firstMob;
                    firstMob = mob;
                    firstMob.next = temp;
                } else {
                    for (temp = firstMob; temp.next != cur; temp = temp.next) {
                    }
                    temp.next = mob;
                    temp.next.next = cur;
                }
            }
        }
    }

    public void isCollision(int mx, int my) {
        if (mx > -1 && mx < mw && my < mh && my > -1) {
            move = true;
            if (Global.INSTANCE.getMap()[mx][my].mIsUsable) {
                switch (Game.getObject(mx, my)) {
                    case 31:
                        fillArea(mx, my, 1, 1, Game.getFloor(mx, my), 32);
                        Global.INSTANCE.getMapview().addLine(getString(R.string.door_opened_message));
                        move = false;
                        turn = false;
                        break;
                    case 33:
                        fillArea(mx, my, 1, 1, Game.getFloor(mx, my), 34);
                        Global.INSTANCE.getMapview().setMDrawLog(false);
                        Global.INSTANCE.getMapview().initProgressBar(33, 159);
                        move = false;
                        turn = false;
                        break;
                    case 36:
                        Global.INSTANCE.getMapview().setMDrawLog(false);
                        Global.INSTANCE.getMapview().initProgressBar(36, 259);
                        move = false;
                        turn = false;
                        break;
                }
            }
            if (turn && Global.INSTANCE.getMap()[mx][my].hasMob())
                attack(Global.INSTANCE.getMap()[mx][my]);
            if (turn)
                if (Game.getObject(mx, my) == 44) {
                    Global.INSTANCE.getHero().modifyStat(5, mRandom.nextInt(3) + 1, -1);
                    Global.INSTANCE.getMapview().addLine(getString(R.string.trap_message));
                    if (Global.INSTANCE.getHero().getStat(5) < 1) {
                        lastAttack = Bitmap.createScaledBitmap(Global.INSTANCE.getTiles()[44].getImg(), 72, 72, false);
                        Global.INSTANCE.getMapview().setMDrawDeathScreen(true);
                    }
                }
            if (!Global.INSTANCE.getMap()[mx][my].mIsPassable && turn) {
                Global.INSTANCE.getMapview().addLine(getString(R.string.path_is_blocked_message));
                v.vibrate(30);
                move = false;
            } else {
                turn = false;
            }
        } else {
            move = false;
        }
    }

    public void move(int mx, int my) {
        Global.INSTANCE.getMapview().setMx(mx);
        Global.INSTANCE.getMapview().setMy(my);
        isCollision(Global.INSTANCE.getHero().mx + mx, Global.INSTANCE.getHero().my + my);
        if (move) {
            Global.INSTANCE.getHero().mx += mx;
            Global.INSTANCE.getHero().my += my;
            Global.INSTANCE.getMapview().setCamx(Global.INSTANCE.getMapview().getCamx() + mx);
            Global.INSTANCE.getMapview().setCamy(Global.INSTANCE.getMapview().getCamy() + my);
        }
        Global.INSTANCE.getMapview().calculateLineOfSight(Global.INSTANCE.getHero().mx, Global.INSTANCE.getHero().my);
        if (!turn) {
            tap = false;
            if (mx == 1) Global.INSTANCE.getHero().isFacingLeft = false;
            if (mx == -1) Global.INSTANCE.getHero().isFacingLeft = true;
        }
        if ((mx != 0 || my != 0) && Global.INSTANCE.getMap()[Global.INSTANCE.getHero().mx][Global.INSTANCE.getHero().my].hasItem()) {
            if (Global.INSTANCE.getMap()[Global.INSTANCE.getHero().mx][Global.INSTANCE.getHero().my].mItems.size() > 1) {
                Global.INSTANCE.getMapview().addLine(getString(R.string.several_items_lying_on_the_ground_message));
            } else {
                Global.INSTANCE.getMapview().addLine(Global.INSTANCE.getMap()[Global.INSTANCE.getHero().mx][Global.INSTANCE.getHero().my].mItems.get(0).mTitle + getString(R.string.lying_on_the_ground_message));
            }
        }
        updateZone();
    }

    public void spread(int i1, int j1, int c) {
        for (int i = i1 - 1; i < i1 + 2; i++)
            for (int j = j1 - 1; j < j1 + 2; j++)
                if (zone[i][j] == defValue && Global.INSTANCE.getMap()[Global.INSTANCE.getMapview().getCamx() - 1 + i][Global.INSTANCE.getMapview().getCamy() - 1 + j].mIsPassable && !Global.INSTANCE.getMap()[Global.INSTANCE.getMapview().getCamx() - 1 + i][Global.INSTANCE.getMapview().getCamy() - 1 + j].hasMob())
                    zone[i][j] = c;
    }

    public void clearZone() {
        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 11; j++)
                zone[i][j] = defValue;
        zone[5][5] = 0;
    }

    public void updateZone() {
        clearZone();
        int xl, xr, yl, yr;
        xl = (Global.INSTANCE.getMapview().getCamx() - 1 < 1) ? 1 : Global.INSTANCE.getMapview().getCamx() - 1;
        yl = (Global.INSTANCE.getMapview().getCamy() - 1 < 1) ? 1 : Global.INSTANCE.getMapview().getCamy() - 1;
        xr = (Global.INSTANCE.getMapview().getCamx() + 10 > mw - 2) ? mw - 2 : Global.INSTANCE.getMapview().getCamx() + 10;
        yr = (Global.INSTANCE.getMapview().getCamy() + 10 > mh - 2) ? mh - 2 : Global.INSTANCE.getMapview().getCamy() + 10;
        for (int c = 0; c < 5; c++)
            for (int i = xl; i < xr; i++)
                for (int j = yl; j < yr; j++)
                    if (zone[i - xl][j - yl] == c)
                        spread(i - xl, j - yl, c + 1);
    }

    // currently not used
    public void pickupItem() {
        v.vibrate(30);
        move(0, 0);
    }

    public void attack(MapClass map) {
        int att = mRandom.nextInt(20) + 1 + Global.INSTANCE.getHero().getStat(11);
        if (att >= map.mob.mob.getMDefense()) {
            int u = mRandom.nextInt(Global.INSTANCE.getHero().getStat(13) - Global.INSTANCE.getHero().getStat(12) + 1) + Global.INSTANCE.getHero().getStat(12) - map.mob.mob.getMArmor();
            if (u < 1) {
                u = 1;
            }
            map.mob.mob.setMHealth(map.mob.mob.getMHealth() - u);
            Global.INSTANCE.getMapview().addLine(map.mob.mob.getName() + getString(R.string.is_receiving_damage_message));
            if (map.mob.mob.getMHealth() < 1) {
                Global.INSTANCE.getMapview().addLine(map.mob.mob.getName() + getString(R.string.is_dying_message));
                Global.INSTANCE.getHero().modifyStat(20, map.mob.t, 1);
                if (map.mob.t == maxMobs - 1)
                    Global.INSTANCE.getMapview().setMDrawWinScreen(true);
                deleteMob(map);
                int x4, y4;
                do {
                    x4 = mRandom.nextInt(Global.INSTANCE.getGame().mw);
                    y4 = mRandom.nextInt(Global.INSTANCE.getGame().mh);
                }
                while (!Global.INSTANCE.getMap()[x4][y4].mIsPassable || Global.INSTANCE.getMap()[x4][y4].mCurrentlyVisible || Global.INSTANCE.getMap()[x4][y4].hasMob());
                int en = mRandom.nextInt(Global.INSTANCE.getGame().maxMobs - curLvls - 1) + curLvls;
                if (en < 3 && mRandom.nextInt(3) == 0) {
                    if (Global.INSTANCE.getMap()[x4 - 1][y4].mIsPassable && !Global.INSTANCE.getMap()[x4 - 1][y4].hasItem())
                        Global.INSTANCE.getGame().createMob(x4 - 1, y4, en);
                    if (Global.INSTANCE.getMap()[x4 + 1][y4].mIsPassable && !Global.INSTANCE.getMap()[x4 + 1][y4].hasItem())
                        Global.INSTANCE.getGame().createMob(x4 + 1, y4, en);
                }
                Global.INSTANCE.getGame().createMob(x4, y4, en);
            }
        } else {
            Global.INSTANCE.getMapview().addLine(getString(R.string.attack_missed_message));
        }
        turn = false;
        move = false;
    }

    public void deleteMob(MapClass map) {
        MobList temp = map.mob;
        if (map.mob == firstMob) {
            firstMob = firstMob.next;
        } else {
            MobList cur;
            for (cur = firstMob; cur.next != map.mob; cur = cur.next) {
            }
            cur.next = map.mob.next;
        }
        temp.map.mob = null;
        temp.map = null;
    }

    public void mobAttack(MobList mob) {
        int att = mRandom.nextInt(20) + 1 + mob.mob.getMArmor();
        if (att >= Global.INSTANCE.getHero().getStat(19)) {
            int u = mob.mob.getMDamage() - Global.INSTANCE.getHero().getStat(22);
            if (u < 1) {
                u = 1;
            }
            Global.INSTANCE.getHero().modifyStat(5, u, -1);
            Global.INSTANCE.getMapview().addLine(mob.mob.getName() + getString(R.string.is_dealing_damage_message));
        } else {
            Global.INSTANCE.getMapview().addLine(mob.mob.getName() + getString(R.string.is_missing_attack_message));
        }
        if (Global.INSTANCE.getHero().getStat(5) < 1) {
            Global.INSTANCE.getHero().modifyStat(5, Global.INSTANCE.getHero().getStat(5), -1);
            lastAttack = Bitmap.createScaledBitmap(mob.getImg(0), 72, 72, false);
            Global.INSTANCE.getMapview().setMDrawDeathScreen(true);
        }
    }

    private void loading() {
        loadStats();
        loadTiles();
        loadItems();
        loadMobs();

        // loading images
        AssetHelper assetHelper = Global.INSTANCE.getMAssetHelper();
        Bitmap temp = assetHelper.getBitmapFromAsset("character_animation_sheet");

        for (int x = 0; x < 4; x++)
            Global.INSTANCE.getHero().img[x] = Bitmap.createBitmap(temp, x * 24, 0, 24, 24);

        bag = Bitmap.createBitmap(assetHelper.getBitmapFromAsset("bag"), 0, 0, 24, 24);
        mCharacterIcon = assetHelper.getBitmapFromAsset("character_icon");
        mInventoryIcon = assetHelper.getBitmapFromAsset("inventory_icon");
        mBackIcon = assetHelper.getBitmapFromAsset("back_icon");
        d = assetHelper.getBitmapFromAsset("ery");
        mSkipTurnIcon = assetHelper.getBitmapFromAsset("skip_turn_icon");

        temp = assetHelper.getBitmapFromAsset("tileset");
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                if (Global.INSTANCE.getTiles()[y * 10 + x] != null) {
                    Global.INSTANCE.getTiles()[y * 10 + x].setImg(Bitmap.createBitmap(temp, x * 24, y * 24, 24, 24));
                }

        temp = assetHelper.getBitmapFromAsset("items_sheet");
        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 10; x++)
                if (y * 10 + x < Global.game.maxItems) {
                    Global.itemDB[y * 10 + x].setImg(Bitmap.createBitmap(temp, x * 24, y * 24, 24, 24));
                }

        temp = assetHelper.getBitmapFromAsset("mobs_sheet");
        for (int x = 0; x < maxMobs; x++) {
            Global.mobDB[x].getImg()[0] = Bitmap.createBitmap(temp, x * 24, 0, 24, 24);
            Global.mobDB[x].getImg()[1] = Bitmap.createBitmap(temp, x * 24, 24, 24, 24);
        }
    }

    private void loadStats() {
        StatsDB[] stats = new StatsDB[Global.INSTANCE.getGame().maxStats];

        try {
            int i = 0;
            XmlPullParser parser = getResources().getXml(R.xml.stats);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("stat")) {
                    String statTitle = parser.getAttributeValue(0);
                    Boolean isSingle = parser.getAttributeValue(1).equals("t");
                    Boolean isMaximum = parser.getAttributeValue(2).equals("t");
                    stats[i++] = new StatsDB(statTitle, isSingle, isMaximum);
                    parser.next();
                }
                parser.next();
            }
        } catch (Throwable t) {
            Log.e("loadStats", t.toString());
        }

        Global.INSTANCE.setStats(stats);
    }

    private void loadTiles() {
        TileDB[] tiles = new TileDB[Global.game.maxTiles];

        try {
            int i = 0;
            XmlPullParser parser = getResources().getXml(R.xml.tiles);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("tile")) {
                    boolean isPassable = parser.getAttributeValue(0).equals("t");
                    boolean isTransparent = parser.getAttributeValue(1).equals("t");
                    boolean isUsable = parser.getAttributeValue(2).equals("t");
                    tiles[i++] = new TileDB(isPassable, isTransparent, isUsable);
                }
                parser.next();
            }
            i = 30;
            parser = getResources().getXml(R.xml.objects);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("object")) {
                    boolean isPassable = parser.getAttributeValue(0).equals("t");
                    boolean isTransparent = parser.getAttributeValue(1).equals("t");
                    boolean isUsable = parser.getAttributeValue(2).equals("t");
                    tiles[i] = new TileDB(isPassable, isTransparent, isUsable);
                    tiles[i++].setMIsWall(parser.getAttributeValue(3).equals("t"));
                }
                parser.next();
            }
        } catch (Throwable t) {
            Log.e("loadTiles", t.toString());
        }

        Global.INSTANCE.setTiles(tiles);
    }

    private void loadItems() {
        ItemDB[] items = new ItemDB[Global.INSTANCE.getGame().maxItems];

        try {
            int i = 0;
            XmlPullParser parser = getResources().getXml(R.xml.items);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && !parser.getName().equals("items")) {
                    int type = 1;
                    String title = parser.getAttributeValue(0);
                    String titleEnding = parser.getAttributeValue(1);
                    int value1 = Integer.parseInt(parser.getAttributeValue(2));
                    int value2 = Integer.parseInt(parser.getAttributeValue(3));
                    int value3 = 0;
                    boolean property = false;

                    switch (parser.getName()) {
                        case "weapon":
                            type = 1;
                            value3 = Integer.parseInt(parser.getAttributeValue(4));
                            property = parser.getAttributeValue(5).equals("t");
                            break;
                        case "shield":
                            type = 2;
                            break;
                        case "armor":
                            type = 3;
                            break;
                        case "item":
                            type = 5;
                            break;
                    }

                    items[i++] = new ItemDB(type, title, titleEnding, value1, value2, value3, property);
                }
                parser.next();
            }
        } catch (Throwable t) {
            Log.e("loadItems", t.toString());
        }

        Global.INSTANCE.setItemDB(items);
    }

    private void loadMobs() {
        MobDB[] mobs = new MobDB[Global.game.maxMobs];

        try {
            int i = 0;
            XmlPullParser parser = getResources().getXml(R.xml.mobs);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("mob")) {
                    String name = parser.getAttributeValue(0);
                    int health = Integer.parseInt(parser.getAttributeValue(1));
                    int attack = Integer.parseInt(parser.getAttributeValue(2));
                    int defense = Integer.parseInt(parser.getAttributeValue(3));
                    int armor = Integer.parseInt(parser.getAttributeValue(4));
                    int speed = Integer.parseInt(parser.getAttributeValue(5));
                    int damage = Integer.parseInt(parser.getAttributeValue(6));
                    mobs[i++] = new MobDB(name, health, attack, defense, armor, speed, damage);
                }
                parser.next();
            }
        } catch (Throwable t) {
            Log.e("loadMobs", t.toString());
        }

        Global.INSTANCE.setMobDB(mobs);
    }

    public void createItem(int x4, int y4) {
        Item item = createItem();
        Global.INSTANCE.getMap()[x4][y4].addItem(item);
    }

    public void createItem(int x4, int y4, int t) {
        Item item = createItem(t);
        Global.INSTANCE.getMap()[x4][y4].addItem(item);
    }

    public void modifyTile(int px, int py, int f, int o) {
        Global.INSTANCE.getMap()[px][py].mIsPassable = Global.INSTANCE.getTiles()[f].getMIsPassable();
        Global.INSTANCE.getMap()[px][py].mIsTransparent = Global.INSTANCE.getTiles()[f].getMIsTransparent();
        Global.INSTANCE.getMap()[px][py].mIsUsable = Global.INSTANCE.getTiles()[f].getMIsUsable();
        Global.INSTANCE.getMap()[px][py].mIsPassable = Global.INSTANCE.getTiles()[o].getMIsPassable();
        Global.INSTANCE.getMap()[px][py].mIsTransparent = Global.INSTANCE.getTiles()[o].getMIsTransparent();
        Global.INSTANCE.getMap()[px][py].mIsUsable = Global.INSTANCE.getTiles()[o].getMIsUsable();
    }

    public void fillArea(int sx, int sy, int lx1, int ly1, int f, int o) {
        for (int y = sy; y < sy + ly1; y++) {
            for (int x = sx; x < sx + lx1; x++) {
                Global.INSTANCE.getMap()[x][y].mFloorID = f;
                Global.INSTANCE.getMap()[x][y].mObjectID = o;
                modifyTile(x, y, f, o);
            }
        }
    }

    public void mobTurn(MobList mob) {
        int a = min(mob);
        if (a == 0) {
            mobAttack(mob);
        } else {
            int x4 = 0, y4 = 0;
            int x = mob.x - Global.INSTANCE.getHero().mx + 5, y = mob.y - Global.INSTANCE.getHero().my + 5;
            boolean u, d, l, r;
            u = d = l = r = false;
            for (int c = -1; c < 2; c++) {
                if (zone[x + c][y - 1] == a) u = true;
                if (zone[x + c][y + 1] == a) d = true;
                if (zone[x - 1][y + c] == a) l = true;
                if (zone[x + 1][y + c] == a) r = true;
            }
            if (l ^ r) {
                if (l) x4 = -1;
                if (r) x4 = 1;
            }
            if (u ^ d) {
                if (u) y4 = -1;
                if (d) y4 = 1;
            }
            if (Global.INSTANCE.getMap()[mob.x + x4][mob.y + y4].mIsPassable && !Global.INSTANCE.getMap()[mob.x + x4][mob.y + y4].hasMob()) {
                Global.INSTANCE.getMap()[mob.x][mob.y].deleteMob();
                mob.x += x4;
                mob.y += y4;
                Global.INSTANCE.getMap()[mob.x][mob.y].addMob(mob);
                updateZone();
            }
        }
    }

    public void newTurnCount() {
        Global.INSTANCE.getHero().init = Global.INSTANCE.getHero().getStat(25);
    }

    public int min(MobList mob) {
        int a = defValue;
        for (int x1 = -1; x1 < 2; x1++)
            for (int y1 = -1; y1 < 2; y1++)
                if (zone[mob.x - Global.INSTANCE.getHero().mx + 5 + x1][mob.y - Global.INSTANCE.getHero().my + 5 + y1] < a)
                    a = zone[mob.x - Global.INSTANCE.getHero().mx + 5 + x1][mob.y - Global.INSTANCE.getHero().my + 5 + y1];
        return a;
    }

    public void mainBody() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (--Global.INSTANCE.getHero().init == 0) {
                        newTurnCount();
                        if (--Global.INSTANCE.getHero().cregen == 0) {
                            Global.INSTANCE.getHero().cregen = Global.INSTANCE.getHero().regen;
                            if (Global.INSTANCE.getHero().getStat(5) != Global.INSTANCE.getHero().getStat(6))
                                Global.INSTANCE.getHero().modifyStat(5, 1, 1);
                        }
                        tap = turn = move = true;
                        while (tap) ;
                    }
                    if (firstMob != null)
                        while (firstMob.turnCount <= turnCount) {
                            MobList temp = firstMob;
                            firstMob = firstMob.next;
                            if (Math.abs(temp.x - Global.INSTANCE.getHero().mx) < 5 && Math.abs(temp.y - Global.INSTANCE.getHero().my) < 5)
                                mobTurn(temp);
                            addInQueue(temp);
                        }
                    turnCount++;
                }
            }
        });
        t.start();
    }

}