package ru.MeatGames.roguelike.tomb;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    public int turnCount = 0;
    public MobList firstMob;

    protected void onCreate(Bundle w) {
        super.onCreate(w);

        Assets.init(this);
        GameController.init(this);

        GameController.start();
    }

    public void onBackPressed() {
        /*if (!Assets.INSTANCE.getMapview().getMDrawProgressBar()) {
            GameController.mHero.interruptAllActions();
            Assets.INSTANCE.getMapview().setMDrawExitDialog(!Assets.INSTANCE.getMapview().getMDrawExitDialog());
        }*/
        GameController.showExitDialog();
    }

    public void exitGame() {
        finish();
    }

    public void createMob(int x, int y, int t) {
        MobList temp = new MobList(t);
        temp.x = x;
        temp.y = y;
        GameController.getMap()[x][y].addMob(temp);
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

    // currently not used
    /*public void pickupItem() {
        Assets.vibrate();
        skipTurn();
    }*/

    // currently not used
    public void createItem(int x4, int y4, int t) {
        //Item item = createItem(t);
        //Assets.INSTANCE.getMap()[x4][y4].addItem(item);
    }

    /*public void mobTurn(MobList mob) {
        int a = min(mob);
        if (a == 0) {
            mobAttack(mob);
        } else {
            int x4 = 0, y4 = 0;
            int x = mob.x - Assets.INSTANCE.getHero().getMx() + 5, y = mob.y - Assets.INSTANCE.getHero().getMy() + 5;
            boolean u, d, l, r;
            u = d = l = r = false;
            for (int c = -1; c < 2; c++) {
                if (currentRoom[x + c][y - 1] == a) u = true;
                if (currentRoom[x + c][y + 1] == a) d = true;
                if (currentRoom[x - 1][y + c] == a) l = true;
                if (currentRoom[x + 1][y + c] == a) r = true;
            }
            if (l ^ r) {
                if (l) x4 = -1;
                if (r) x4 = 1;
            }
            if (u ^ d) {
                if (u) y4 = -1;
                if (d) y4 = 1;
            }
            if (Assets.INSTANCE.getMap()[mob.x + x4][mob.y + y4].mIsPassable && !Assets.INSTANCE.getMap()[mob.x + x4][mob.y + y4].hasMob()) {
                Assets.INSTANCE.getMap()[mob.x][mob.y].deleteMob();
                mob.x += x4;
                mob.y += y4;
                Assets.INSTANCE.getMap()[mob.x][mob.y].addMob(mob);
                updateZone();
            }
        }
    }

    public void updateHeroTurnCount(Boolean isResting) {
        Assets.INSTANCE.getHero().setInit(Assets.INSTANCE.getHero().getStat(25));
        if (isResting) {
            Assets.INSTANCE.getHero().setInit(Assets.INSTANCE.getHero().getInit() / 2);
        }
    }

    public int min(MobList mob) {
        int a = defValue;
        for (int x1 = -1; x1 < 2; x1++)
            for (int y1 = -1; y1 < 2; y1++)
                if (currentRoom[mob.x - Assets.INSTANCE.getHero().getMx() + 5 + x1][mob.y - Assets.INSTANCE.getHero().getMy() + 5 + y1] < a)
                    a = currentRoom[mob.x - Assets.INSTANCE.getHero().getMx() + 5 + x1][mob.y - Assets.INSTANCE.getHero().getMy() + 5 + y1];
        return a;
    }*/

}