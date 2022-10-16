package ru.MeatGames.roguelike.tomb.model;

import java.util.LinkedList;

import ru.MeatGames.roguelike.tomb.Assets;
import ru.MeatGames.roguelike.tomb.MobList;

public class MapClass {

    public int mFloorID;
    public int mObjectID;
    public LinkedList<Item> mItems;
    public MobList mob;
    public boolean mIsPassable;
    public boolean mIsTransparent;
    public boolean mCurrentlyVisible;
    public boolean mIsDiscovered;
    public boolean mIsUsable;

    public MapClass() {
        mCurrentlyVisible = false;
        mIsTransparent = true;
        mIsPassable = true;
        mIsDiscovered = false;
        mItems = new LinkedList<>();
    }

    public void addItem(Item item) {
        mItems.add(item);
    }

    public Item getItem() {
        return mItems.pop();
    }

    public void deleteItems() {
        mItems.clear();
    }

    public void addMob(MobList mob) {
        mob.map = this;
        this.mob = mob;
    }

    public void deleteMob() {
        mob = null;
    }

    public boolean hasItem() {
        return mItems.size() != 0;
    }

    public boolean hasMob() {
        return mob != null;
    }

    public boolean isWall() {
        return Assets.INSTANCE.getObjects()[mObjectID].getMIsWall();
    }

}