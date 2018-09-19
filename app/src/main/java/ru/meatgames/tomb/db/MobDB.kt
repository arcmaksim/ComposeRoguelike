package ru.meatgames.tomb.db

import android.graphics.Rect
import ru.meatgames.tomb.model.MobClass

class MobDB {

    var mob: MobClass
    var img: Array<Rect?>

    constructor(name: String, health: Int, attack: Int,
                defence: Int, armor: Int, speed: Int, damage: Int) {
        mob = MobClass(name)
        mob.mHealth = health
        mob.mAttack = attack
        mob.mDefense = defence
        mob.mArmor = armor
        mob.mSpeed = speed
        mob.mDamage = damage
    }

    init {
        img = arrayOfNulls<Rect>(2)
    }

}