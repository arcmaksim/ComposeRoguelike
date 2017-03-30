package ru.MeatGames.roguelike.tomb.util

import android.app.Activity
import ru.MeatGames.roguelike.tomb.R

object LogHelper {

    private lateinit var mActivity: Activity

    private lateinit var SEARCH_CHEST: String
    private lateinit var SEARCH_BOOKSHELF: String
    private lateinit var EXPERIENCE_EARNED: String
    private lateinit var NOTHING_INTERESTING: String
    private lateinit var TURN_PASSED: String
    private lateinit var DOOR_OPENED: String
    private lateinit var PATH_IS_BLOCKED: String
    private lateinit var ATTACK_MISSED: String
    private lateinit var SEVERAL_ITEMS_ON_THE_GROUND: String
    private lateinit var TRAP: String

    fun init(activity: Activity) {
        mActivity = activity

        SEARCH_CHEST = mActivity.getString(R.string.search_chest_message)
        SEARCH_BOOKSHELF = mActivity.getString(R.string.search_bookshelf_message)
        EXPERIENCE_EARNED = mActivity.getString(R.string.experience_earned_message)
        NOTHING_INTERESTING = mActivity.getString(R.string.nothing_interesting_message)
        TURN_PASSED = mActivity.getString(R.string.turn_passed_message)
        DOOR_OPENED = mActivity.getString(R.string.door_opened_message)
        PATH_IS_BLOCKED = mActivity.getString(R.string.path_is_blocked_message)
        ATTACK_MISSED = mActivity.getString(R.string.attack_missed_message)
        SEVERAL_ITEMS_ON_THE_GROUND = mActivity.getString(R.string.several_items_lying_on_the_ground_message)
        TRAP = mActivity.getString(R.string.trap_message)
    }

}