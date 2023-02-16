package ru.meatgames.tomb

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import dagger.hilt.android.AndroidEntryPoint
import ru.meatgames.tomb.GameController.getMap2
import ru.meatgames.tomb.model.provider.GameDataProvider
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var turnCount = 0
    var firstMob: MobList? = null

    @Inject
    lateinit var gameController: ru.meatgames.tomb.domain.GameController

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFullScreenMode()
        NewAssets.loadAssets(this)
        GameDataProvider.init(this)
    
        setContent {
            TombApp(::finish)
        }
    }

    private fun setupFullScreenMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)
        /** TODO: saving game
         * 1. Save map
         * 1.1. Save consumables
         * 1.2. Save creatures
         * 2. Save hero
         * 2.1. Save map position and stats
         * 2.2. Save inventory content and it's state
         * 3. Save game state and current game screen state
         */
    }

    fun exitGame() {
        finish()
    }

    fun createMob(
        x: Int,
        y: Int,
        t: Int
    ) {
        val temp = MobList(t)
        temp.x = x
        temp.y = y
        getMap2()[x][y].addMob(temp)
        addInQueue(temp)
    }

    fun addInQueue(mob: MobList) {
        mob.turnCount = turnCount + mob.mob.mSpeed
        if (firstMob == null) {
            firstMob = mob
        } else {
            var cur: MobList
            var b = false
            cur = firstMob!!
            while (cur.turnCount <= mob.turnCount) {
                if (cur.next == null) {
                    b = true
                    break
                }
                cur = cur.next
            }
            if (b) {
                cur.next = mob
                cur.next.next = null
            } else {
                var temp: MobList
                if (cur === firstMob) {
                    temp = firstMob!!
                    firstMob = mob
                    firstMob!!.next = temp
                } else {
                    temp = firstMob!!
                    while (temp.next !== cur) {
                        temp = temp.next
                    }
                    temp.next = mob
                    temp.next.next = cur
                }
            }
        }
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
