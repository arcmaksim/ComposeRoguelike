package ru.meatgames.tomb.old_model;

import android.graphics.Rect;

import ru.meatgames.tomb.Assets;

public class Item {

    public String mTitle; //Name
    public String mTitleEnding; //окончание
    public int mWeight; //Weight
    public int mValue; //Value
    public int mType; //mType
    private int id; //something with itemDB
    public int mValue1;
    public int mValue2;
    public int mValue3;
    public boolean mProperty; // currently for weapons only - is twohanded

    public Item() {
        mValue1 = mValue2 = mValue3 = -10000;
        mProperty = false;
    }

    public Item(int id) {
        mValue1 = mValue2 = mValue3 = -10000;
        mProperty = false;
        this.id = id;
        this.mTitle = Assets.INSTANCE.getItemDB()[id].mTitle;
        this.mTitleEnding = Assets.INSTANCE.getItemDB()[id].mTitleEnding;
        this.mType = Assets.INSTANCE.getItemDB()[id].mType;
        this.mValue1 = Assets.INSTANCE.getItemDB()[id].mValue1;
        this.mValue2 = Assets.INSTANCE.getItemDB()[id].mValue2;
        this.mValue3 = Assets.INSTANCE.getItemDB()[id].mValue3;
        this.mProperty = Assets.INSTANCE.getItemDB()[id].mProperty;
    }

    public Item(int type, String title, String titleEndings,
                int value1, int value2, int value3, boolean property) {
        mType = type;
        mTitle = title;
        mTitleEnding = titleEndings;
        mValue1 = value1;
        mValue2 = value2;
        mValue3 = value3;
        mProperty = property;
    }

    public boolean isWeapon() {
        return mType == 1;
    }

    public boolean isShield() {
        return mType == 2;
    }

    public boolean isArmor() {
        return mType == 3;
    }

    public boolean isGear() {
        return mType == 4;
    }

    public boolean isConsumable() {
        return mType == 5;
    }

    public Rect getImage() {
        return Assets.INSTANCE.getAssetRect(id);
    }

}
