package ru.MeatGames.roguelike.tomb;

import android.graphics.Rect;

import ru.MeatGames.roguelike.tomb.model.MapClass;
import ru.MeatGames.roguelike.tomb.model.MobClass;

public class MobList {

	public MobList next;
    public MobClass mob;
    public MapClass map;
    public int t;
    public int turnCount;
	public int x;
	public int y;

    public Rect getImg(int time) {
        return Assets.INSTANCE.getAssetRect(t);
    }

    public MobList(int t) {
        this.t = t;
        mob = new MobClass();
        mob.setMHealth(Assets.INSTANCE.getMobDB()[t].getMob().getMHealth());
        mob.setName(Assets.INSTANCE.getMobDB()[t].getMob().getName());
        mob.setMDefense(Assets.INSTANCE.getMobDB()[t].getMob().getMDefense());
        mob.setMArmor(Assets.INSTANCE.getMobDB()[t].getMob().getMDefense());
        mob.setMSpeed(Assets.INSTANCE.getMobDB()[t].getMob().getMSpeed());
    }
}