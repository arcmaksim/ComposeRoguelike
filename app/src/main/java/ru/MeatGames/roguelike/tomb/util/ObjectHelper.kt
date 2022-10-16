package ru.MeatGames.roguelike.tomb.util

object ObjectHelper {

    val EMPTY_OBJECT = 0
    val WALL = 1
    val DOOR_CLOSED = 2
    val DOOR_OPENED = 3
    val CHEST_CLOSED = 4
    val CHEST_OPENED = 5
    val CHEST_EMPTY = 6
    val BOOKSHELF_FULL = 7
    val BOOKSHELF_EMPTY = 8
    val TABLE = 9
    val STAIRS_UP = 10
    val STAIRS_DOWN = 11
    val CHAIR = 12
    val STATUE = 13
    val SPIKE_TRAP_TRIGGERED = 14
    val SPIKE_TRAP_HIDDEN = 15
    val TABLE_WITH_PAPERS = 16

    private val mWalls = arrayOf(WALL)

    fun isWall(objectId: Int) = mWalls.contains(objectId)

}