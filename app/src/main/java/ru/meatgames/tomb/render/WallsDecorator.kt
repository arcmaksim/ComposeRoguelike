package ru.meatgames.tomb.render

import ru.meatgames.tomb.domain.render.BufferHolder
import ru.meatgames.tomb.domain.render.BufferHolderFactory
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import javax.inject.Inject

class WallsDecorator @Inject constructor(
    private val bufferHolderFactory: BufferHolderFactory,
) : MapRenderTilesDecorator {

    override fun apply() {
        val bufferHolder = bufferHolderFactory.cachedBufferHolder

        var index = -1

        bufferHolder.mapBuffer.iterator().forEach { tile ->
            index++

            val objectEntityTile = tile?.objectEntityTile ?: return@forEach
            if (!objectEntityTile.isWall()) return@forEach

            val wallFlags = bufferHolder.calcWallsFlags(index)
            bufferHolder.filterAngles(
                wallFlags = wallFlags,
                index = index,
            )
        }
    }

    private fun BufferHolder.calcWallsFlags(
        index: Int,
    ): Int {
        var wallFlags = 0

        // Top
        if (mapBuffer.getOrNull(index - width)?.objectEntityTile?.isWall() == true) {
            wallFlags += 1
        }
        // Right
        if (mapBuffer.getOrNull(index + 1)?.objectEntityTile?.isWall() == true) {
            wallFlags += 2
        }
        // Bottom
        if (mapBuffer.getOrNull(index + width)?.objectEntityTile?.isWall() == true) {
            wallFlags += 4
        }
        // Left
        if (mapBuffer.getOrNull(index - 1)?.objectEntityTile?.isWall() == true) {
            wallFlags += 8
        }

        return wallFlags
    }

    private fun BufferHolder.filterAngles(
        wallFlags: Int,
        index: Int,
    ) {
        val wallRenderTile = wallFlags.toWallRenderTile()

        val x = index % width
        val y = index / width

        val deltaX = (x - horizontalCenter).coerceIn(minimumValue = -1, maximumValue = 1)
        val deltaY = (y - verticalCenter).coerceIn(minimumValue = -1, maximumValue = 1)

        objectRenderingBuffer[index] = when (wallRenderTile) {
            ObjectRenderTile.Wall15 -> (wallFlags - calcFilterForWall15(deltaX = deltaX, deltaY = deltaY)).toWallRenderTile()
            ObjectRenderTile.Wall7 -> filterWall7(deltaX = deltaX, deltaY = deltaY)
            ObjectRenderTile.Wall11 -> filterWall11(deltaX = deltaX, deltaY = deltaY)
            ObjectRenderTile.Wall14 -> filterWall14(deltaX = deltaX, deltaY = deltaY)
            ObjectRenderTile.Wall13 -> filterWall13(deltaX = deltaX, deltaY = deltaY)
            else -> wallRenderTile
        }
    }

    private fun guardDeltas(
        deltaX: Int,
        deltaY: Int,
    ) {
        require(deltaX >= -1 && deltaX <= 1 && deltaY >= -1 && deltaY <= 1) {
            "Only -1, 0, 1 are allowed"
        }
    }

    private fun filterWall7(
        deltaX: Int,
        deltaY: Int,
    ): ObjectRenderTile {
        if (deltaX == 0 && deltaY == 0) return ObjectRenderTile.Wall7
        guardDeltas(deltaX, deltaY)

        return when {
            deltaX == 1 -> ObjectRenderTile.Wall5
            deltaY == -1 -> ObjectRenderTile.Wall6
            deltaY == 1 -> ObjectRenderTile.Wall3
            else -> ObjectRenderTile.Wall7
        }
    }

    private fun filterWall11(
        deltaX: Int,
        deltaY: Int,
    ): ObjectRenderTile {
        if (deltaX == 0 && deltaY == 0) return ObjectRenderTile.Wall11
        guardDeltas(deltaX, deltaY)

        return when {
            deltaY == -1 -> ObjectRenderTile.Wall10
            deltaX == 1 -> ObjectRenderTile.Wall9
            deltaX == -1 -> ObjectRenderTile.Wall3
            else -> ObjectRenderTile.Wall11
        }
    }

    private fun filterWall14(
        deltaX: Int,
        deltaY: Int,
    ): ObjectRenderTile {
        if (deltaX == 0 && deltaY == 0) return ObjectRenderTile.Wall14
        guardDeltas(deltaX, deltaY)

        return when {
            deltaY == 1 -> ObjectRenderTile.Wall10
            deltaX == 1 -> ObjectRenderTile.Wall12
            deltaX == -1 -> ObjectRenderTile.Wall6
            else -> ObjectRenderTile.Wall14
        }
    }

    private fun filterWall13(
        deltaX: Int,
        deltaY: Int,
    ): ObjectRenderTile {
        if (deltaX == 0 && deltaY == 0) return ObjectRenderTile.Wall13
        guardDeltas(deltaX, deltaY)

        return when {
            deltaX == -1 -> ObjectRenderTile.Wall5
            deltaY == 1 -> ObjectRenderTile.Wall9
            deltaY == -1 -> ObjectRenderTile.Wall12
            else -> ObjectRenderTile.Wall13
        }
    }

    private fun calcFilterForWall15(
        deltaX: Int,
        deltaY: Int,
    ): Int {
        guardDeltas(deltaX, deltaY)
        val horizontalFilter = when (deltaX) {
            1 -> 2
            -1 -> 8
            else -> 0
        }
        val verticalFilter = when (deltaY) {
            1 -> 4
            -1 -> 1
            else -> 0
        }
        return horizontalFilter + verticalFilter
    }

    private fun Int.toWallRenderTile(): ObjectRenderTile = when (this) {
        0 -> ObjectRenderTile.Wall0
        1 -> ObjectRenderTile.Wall1
        2 -> ObjectRenderTile.Wall2
        3 -> ObjectRenderTile.Wall3
        4 -> ObjectRenderTile.Wall4
        5 -> ObjectRenderTile.Wall5
        6 -> ObjectRenderTile.Wall6
        7 -> ObjectRenderTile.Wall7
        8 -> ObjectRenderTile.Wall8
        9 -> ObjectRenderTile.Wall9
        10 -> ObjectRenderTile.Wall10
        11 -> ObjectRenderTile.Wall11
        12 -> ObjectRenderTile.Wall12
        13 -> ObjectRenderTile.Wall13
        14 -> ObjectRenderTile.Wall14
        15 -> ObjectRenderTile.Wall15
        else -> throw IllegalArgumentException("Unknown wall flags value: $this")
    }

    private fun ObjectEntityTile.isWall(): Boolean = when (this) {
        ObjectEntityTile.Wall -> true
        else -> false
    }

}
