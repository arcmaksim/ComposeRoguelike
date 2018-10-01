package ru.meatgames.tomb

import android.graphics.Rect
import java.util.*

class AreaGenerator {

    private val minAreaWidth: Int = 6
    private val minAreaHeight: Int = 6
    private val minAreaSquare: Int = 40
    private val targetRoomRatio: Float = .75F


    private val isAreaDividable: (Rect) -> Boolean = { area ->
        with (area) {
            return@with (width() >= minAreaWidth * 2 || height() >= minAreaHeight * 2) && width() * height() >= minAreaSquare
        }
    }


    fun generateAreas(initialArea: Rect = Rect(0, 0, 63, 63)): List<Rect> {
        val targetRoomCount = ((initialArea.width() * initialArea.height()) / minAreaSquare * targetRoomRatio).toInt()

        val random = Random()
        val areas: MutableList<Rect> = mutableListOf(initialArea)
        val availableAreas: MutableList<Rect> = mutableListOf(initialArea)
        while (true) {
            val area = availableAreas[random.nextInt(availableAreas.size)]
            val (firstArea, secondArea) =
                    when (area.width() > area.height()) {
                        true -> {
                            val splitRange = area.width() - 2 * minAreaWidth
                            val split = if (splitRange != 0) random.nextInt(splitRange) else 0
                            val firstArea = Rect(area.left, area.top, area.left + minAreaWidth + split, area.bottom)
                            val secondArea = Rect(area.left + firstArea.width(), area.top, area.right, area.bottom)
                            Pair(firstArea, secondArea)
                        }
                        else -> {
                            val splitRange = area.height() - 2 * minAreaHeight
                            val split = if (splitRange != 0) random.nextInt(splitRange) else 0
                            val firstArea = Rect(area.left, area.top, area.right, area.top + minAreaHeight + split)
                            val secondArea = Rect(area.left, area.top + firstArea.height(), area.right, area.bottom)
                            Pair(firstArea, secondArea)
                        }
                    }

            if (isAreaDividable(firstArea)) availableAreas.add(firstArea)
            else areas.add(firstArea)

            if (isAreaDividable(secondArea)) availableAreas.add(secondArea)
            else areas.add(secondArea)

            areas.remove(area)
            availableAreas.remove(area)
            if (availableAreas.isEmpty() || availableAreas.size >= targetRoomCount) break
        }

        return areas
    }

}