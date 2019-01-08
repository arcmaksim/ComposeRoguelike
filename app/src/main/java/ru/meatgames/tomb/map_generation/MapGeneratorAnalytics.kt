package ru.meatgames.tomb.map_generation

import android.util.Log

class MapGeneratorAnalytics {

    private var placedRoomCount: Int = 0
    private var triesOfRoomPlacement: Int = 0


    fun incPlacedRooms() {
        placedRoomCount++
    }

    fun incRoomPlacementTries() {
        triesOfRoomPlacement++
    }

    fun printStatistics() {
        Log.d("MapGeneration", "- MapGeneration -------------------")
        Log.d("MapGeneration", "room placed: $placedRoomCount")
        Log.d("MapGeneration", "number of rooms placement tries: $triesOfRoomPlacement")
    }

    fun clear() {
        placedRoomCount = 0
        triesOfRoomPlacement = 0
    }

}
