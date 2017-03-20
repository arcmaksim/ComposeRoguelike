package ru.MeatGames.roguelike.tomb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import java.util.Random;

import ru.MeatGames.roguelike.tomb.model.Item;
import ru.MeatGames.roguelike.tomb.model.MapClass;
import ru.MeatGames.roguelike.tomb.screen.CharacterScreen;
import ru.MeatGames.roguelike.tomb.screen.DeathScreen;
import ru.MeatGames.roguelike.tomb.screen.DetailedItemScreen;
import ru.MeatGames.roguelike.tomb.screen.GameScreen;
import ru.MeatGames.roguelike.tomb.screen.GearScreen;
import ru.MeatGames.roguelike.tomb.screen.InventoryScreen;
import ru.MeatGames.roguelike.tomb.screen.MainMenu;
import ru.MeatGames.roguelike.tomb.screen.MapScreen;
import ru.MeatGames.roguelike.tomb.screen.Screens;
import ru.MeatGames.roguelike.tomb.util.AssetHelper;
import ru.MeatGames.roguelike.tomb.util.MapHelper;
import ru.MeatGames.roguelike.tomb.util.ScreenHelper;

public class Game extends Activity {

    public static int curLvls = 0;

    public final int mTileSize = 24; // in pixels
    private float mScaleAmount;
    private float mActualTileSize;

    public final int maxMobs = 6;
    public final int defValue = 99;
    public final int maxLvl = 3;
    public int turnCount = 0;
    public Random mRandom;
    public MobList firstMob;
    public int[][] zone;

    // TODO: temporal solution
    public Item selectedItem;
    private Screens lastScreen;

    private Thread mMainGameThread;
    private MainGameLoop mMainGameLoop;

    // player flags
    public boolean mIsPlayerTurn = true;
    public boolean mIsPlayerMoved = false;
    public boolean mAcceptPlayerInput;

    public Bitmap mCharacterIcon;
    public Bitmap mInventoryIcon;
    public Bitmap mBackIcon;
    public Bitmap d;
    public Bitmap mSkipTurnIcon;
    public Bitmap lastAttack;
    public Bitmap bag;

    public boolean mDrawInputAreas = false;

    public static int getFloor(int x, int y) {
        return Global.INSTANCE.getMap()[x][y].mFloorID;
    }

    public static int getObject(int x, int y) {
        return Global.INSTANCE.getMap()[x][y].mObjectID;
    }

    protected void onCreate(Bundle w) {
        super.onCreate(w);
        Global.initGame(this);

        mRandom = new Random();
        zone = new int[11][11];

        Global.mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        calculateTileSize();

        Global.INSTANCE.setMmview(new MainMenu(this));
        Global.INSTANCE.setMAssetHelper(new AssetHelper(getAssets()));

        changeScreen(Screens.MAIN_MENU);
    }

    private void calculateTileSize() {
        Point screenSize = ScreenHelper.getScreenSize(getWindowManager());
        mScaleAmount = screenSize.x / (mTileSize * 10f);
        mActualTileSize = mTileSize * mScaleAmount;
    }

    public float getScaleAmount() {
        return mScaleAmount;
    }

    public float getActualTileSize() {
        return mActualTileSize;
    }

    public void onBackPressed() {
        if (!Global.INSTANCE.getMapview().getMDrawProgressBar()) {
            Global.INSTANCE.getHero().interruptAllActions();
            Global.INSTANCE.getMapview().setMDrawExitDialog(!Global.INSTANCE.getMapview().getMDrawExitDialog());
        }
    }

    public void exitGame() {
        finish();
    }

    public void newGame() {
        Global.initInitialData();
        Global.newHero();
        curLvls = 0;
        generateNewMap();
        Global.INSTANCE.getMapview().clearLog();
        newGameLoop();
        changeScreen(Screens.GAME_SCREEN);
    }

    public void changeScreen(Screens screen) {
        View view;
        switch (screen) {
            case GAME_SCREEN:
                view = Global.INSTANCE.getMapview();
                ((GameScreen) view).updateMapBuffer();
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
            default:
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
            case DEATH_SCREEN:
                view = new DeathScreen(this);
                break;
        }

        // TODO: temporal solution
        if (Global.INSTANCE.getHero() != null) {
            Global.INSTANCE.getHero().interruptAllActions();
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
        MapClass mapCell = MapHelper.getMapCell(mx, my);
        if (mapCell != null) {
            if (mapCell.mIsUsable) {
                switch (Game.getObject(mx, my)) {
                    case 2:
                        MapHelper.changeObject(mx, my, 3);
                        Global.INSTANCE.getMapview().addLine(getString(R.string.door_opened_message));
                        break;
                    case 4:
                        MapHelper.changeObject(mx, my, 5);
                        Global.INSTANCE.getMapview().setMDrawLog(false);
                        Global.INSTANCE.getMapview().initProgressBar(4, 159);
                        break;
                    case 7:
                        Global.INSTANCE.getMapview().setMDrawLog(false);
                        Global.INSTANCE.getMapview().initProgressBar(7, 259);
                        break;
                }
                mIsPlayerTurn = false;
                return;
            }
            if (mIsPlayerTurn && mapCell.hasMob()) {
                attack(mapCell);
                return;
            }
            if (mapCell.mIsPassable) {
                mIsPlayerTurn = false;
                mIsPlayerMoved = true; // ?
                if (mapCell.mObjectID == 15) {
                    Global.INSTANCE.getHero().modifyStat(5, mRandom.nextInt(3) + 1, -1);
                    Global.INSTANCE.getMapview().addLine(getString(R.string.trap_message));
                    if (Global.INSTANCE.getHero().getStat(5) < 1) {
                        lastAttack = Bitmap.createScaledBitmap(Global.INSTANCE.getObjects()[15].getImg(), 72, 72, false);
                        changeScreen(Screens.DEATH_SCREEN);
                    }
                }
            } else {
                Global.INSTANCE.getMapview().addLine(getString(R.string.path_is_blocked_message));
                Global.vibrate();
            }
        }
    }

    public void skipTurn() {
        mAcceptPlayerInput = false;
        mIsPlayerTurn = false;
        updateZone();
    }

    public void move(int mx, int my) {
        Global.INSTANCE.getHero().interruptResting();
        Global.INSTANCE.getMapview().setMx(mx);
        Global.INSTANCE.getMapview().setMy(my);
        isCollision(Global.INSTANCE.getHero().getMx() + mx, Global.INSTANCE.getHero().getMy() + my);
        if (mIsPlayerMoved) {
            Global.INSTANCE.getHero().setMx(Global.INSTANCE.getHero().getMx() + mx);
            Global.INSTANCE.getHero().setMy(Global.INSTANCE.getHero().getMy() + my);
            Global.INSTANCE.getMapview().setCamx(Global.INSTANCE.getMapview().getCamx() + mx);
            Global.INSTANCE.getMapview().setCamy(Global.INSTANCE.getMapview().getCamy() + my);
        }
        Global.INSTANCE.getMapview().calculateLineOfSight(Global.INSTANCE.getHero().getMx(), Global.INSTANCE.getHero().getMy());
        if (mx == 1) Global.INSTANCE.getHero().setMIsFacingLeft(false);
        if (mx == -1) Global.INSTANCE.getHero().setMIsFacingLeft(true);
        if ((mx != 0 || my != 0) && Global.INSTANCE.getMap()[Global.INSTANCE.getHero().getMx()][Global.INSTANCE.getHero().getMy()].hasItem()) {
            if (Global.INSTANCE.getMap()[Global.INSTANCE.getHero().getMx()][Global.INSTANCE.getHero().getMy()].mItems.size() > 1) {
                Global.INSTANCE.getMapview().addLine(getString(R.string.several_items_lying_on_the_ground_message));
            } else {
                Global.INSTANCE.getMapview().addLine(Global.INSTANCE.getMap()[Global.INSTANCE.getHero().getMx()][Global.INSTANCE.getHero().getMy()].mItems.get(0).mTitle + getString(R.string.lying_on_the_ground_message));
            }
        }
        updateZone();
        Global.INSTANCE.getMapview().updateMapBuffer();
    }

    public void spread(int i1, int j1, int c) {
        for (int i = i1 - 1; i < i1 + 2; i++)
            for (int j = j1 - 1; j < j1 + 2; j++)
                if (zone[i][j] == defValue
                        && Global.INSTANCE.getMap()[Global.INSTANCE.getMapview().getCamx() - 1 + i][Global.INSTANCE.getMapview().getCamy() - 1 + j].mIsPassable
                        && !Global.INSTANCE.getMap()[Global.INSTANCE.getMapview().getCamx() - 1 + i][Global.INSTANCE.getMapview().getCamy() - 1 + j].hasMob())
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
        xr = (Global.INSTANCE.getMapview().getCamx() + 10 > MapHelper.INSTANCE.getMapWidth() - 2)
                ? MapHelper.INSTANCE.getMapHeight() - 2
                : Global.INSTANCE.getMapview().getCamx() + 10;
        yr = (Global.INSTANCE.getMapview().getCamy() + 10 > MapHelper.INSTANCE.getMapWidth() - 2)
                ? MapHelper.INSTANCE.getMapHeight() - 2
                : Global.INSTANCE.getMapview().getCamy() + 10;
        for (int c = 0; c < 5; c++)
            for (int i = xl; i < xr; i++)
                for (int j = yl; j < yr; j++)
                    if (zone[i - xl][j - yl] == c)
                        spread(i - xl, j - yl, c + 1);
    }

    // currently not used
    public void pickupItem() {
        Global.vibrate();
        skipTurn();
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
                    x4 = mRandom.nextInt(MapHelper.INSTANCE.getMapWidth());
                    y4 = mRandom.nextInt(MapHelper.INSTANCE.getMapHeight());
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
        mIsPlayerTurn = false;
    }

    public void deleteMob(MapClass map) {
        MobList temp = map.mob;
        if (map.mob == firstMob) {
            firstMob = firstMob.next;
        } else {
            MobList cur;
            for (cur = firstMob; cur.next != map.mob; cur = cur.next) {}
            cur.next = map.mob.next;
        }
        temp.map.mob = null;
        temp.map = null;
    }

    public void mobAttack(MobList mob) {
        Global.INSTANCE.getHero().interruptResting();

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeScreen(Screens.DEATH_SCREEN);
                }
            });
        }
    }

    public void createItem(int x4, int y4) {
        Item item = createItem();
        Global.INSTANCE.getMap()[x4][y4].addItem(item);
    }

    // currently not used
    public void createItem(int x4, int y4, int t) {
        Item item = createItem(t);
        Global.INSTANCE.getMap()[x4][y4].addItem(item);
    }

    public void mobTurn(MobList mob) {
        int a = min(mob);
        if (a == 0) {
            mobAttack(mob);
        } else {
            int x4 = 0, y4 = 0;
            int x = mob.x - Global.INSTANCE.getHero().getMx() + 5, y = mob.y - Global.INSTANCE.getHero().getMy() + 5;
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

    public void updateHeroTurnCount(Boolean isResting) {
        Global.INSTANCE.getHero().setInit(Global.INSTANCE.getHero().getStat(25));
        if (isResting) {
            Global.INSTANCE.getHero().setInit(Global.INSTANCE.getHero().getInit() / 2);
        }
    }

    public int min(MobList mob) {
        int a = defValue;
        for (int x1 = -1; x1 < 2; x1++)
            for (int y1 = -1; y1 < 2; y1++)
                if (zone[mob.x - Global.INSTANCE.getHero().getMx() + 5 + x1][mob.y - Global.INSTANCE.getHero().getMy() + 5 + y1] < a)
                    a = zone[mob.x - Global.INSTANCE.getHero().getMx() + 5 + x1][mob.y - Global.INSTANCE.getHero().getMy() + 5 + y1];
        return a;
    }

    public void newGameLoop() {
        mMainGameLoop = new MainGameLoop();
        mMainGameThread = new Thread(mMainGameLoop);
        mMainGameThread.start();
    }

    public void gameOver() {
        if (mMainGameThread != null) {
            mMainGameLoop.terminate();
        }
        mMainGameThread = null;
        changeScreen(Screens.MAIN_MENU);
    }

}