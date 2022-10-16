package ru.MeatGames.roguelike.tomb.model;

data class MobClass(var name: String = "") {

    var mHealth: Int = 0
    var mAttack: Int = 0
    var mDefense: Int = 0
    var mArmor: Int = 0
    var mSpeed: Int = 0
    var mDamage: Int = 0

}