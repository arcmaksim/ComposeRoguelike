package ru.MeatGames.roguelike.tomb.util

import android.util.Log

object FPSLogger {

    private val mChunkSize = 10
    private var mCurrentCell = 0
    val mFpsLog: Array<Long> = Array(mChunkSize, { 0L })
    var mTag: String = "FPS"

    fun addEntry(value: Long) {
        if (mCurrentCell == mChunkSize) {
            mCurrentCell = 0
            val totalTime: Long = (0..mChunkSize - 1)
                    .map { mFpsLog[it] }
                    .sum()
            val averageFrameTime = (totalTime / mChunkSize).toFloat()
            val fps = 1000000000 / averageFrameTime
            Log.d(mTag, "FPS: $fps, $averageFrameTime")
        }

        mFpsLog[mCurrentCell++] = value
    }

}