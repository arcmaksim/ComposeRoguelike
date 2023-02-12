package ru.meatgames.tomb.render

import ru.meatgames.tomb.domain.RenderTiles
import ru.meatgames.tomb.domain.ScreenSpaceRenderTiles
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import javax.inject.Inject

class WallsDecorator @Inject constructor() : MapRenderTilesDecorator {
    
    override fun processMapRenderTiles(
        mapRenderTiles: List<ScreenSpaceRenderTiles?>,
        tileLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?> = mapRenderTiles.mapIndexed { index, pair ->
        val objectRenderTile = pair?.second?.second ?: return@mapIndexed pair
        if (!objectRenderTile.isWall()) return@mapIndexed pair

        val updatedWallRenderTile = mapRenderTiles.calcWallsFlags(index, tileLineWidth)
            .filterAngles(index, tileLineWidth, mapRenderTiles.size)
        
        pair.first to RenderTiles(pair.second.first, updatedWallRenderTile)
    }

    private fun List<ScreenSpaceRenderTiles?>.calcWallsFlags(
        tileIndex: Int,
        tilesLineWidth: Int,
    ): Int {
        var wallFlags = 0

        // Top
        if (getOrNull(tileIndex - tilesLineWidth)?.second?.second?.isWall() == true) {
            wallFlags += 1
        }
        // Right
        if (getOrNull(tileIndex + 1)?.second?.second?.isWall() == true) {
            wallFlags += 2
        }
        // Bottom
        if (getOrNull(tileIndex + tilesLineWidth)?.second?.second?.isWall() == true) {
            wallFlags += 4
        }
        // Left
        if (getOrNull(tileIndex - 1)?.second?.second?.isWall() == true) {
            wallFlags += 8
        }

        return wallFlags
    }

    private fun Int.filterAngles(
        tileIndex: Int,
        tilesLineWidth: Int,
        maxIndex: Int
    ): ObjectRenderTile {
        val wallRenderTile = toWallRenderTile()

        val x = tileIndex % tilesLineWidth
        val verticalMiddlePoint = tilesLineWidth / 2

        val y = tileIndex / tilesLineWidth
        val horizontalMiddlePoint = maxIndex / tilesLineWidth / 2

        val deltaX = (x - verticalMiddlePoint).coerceIn(minimumValue = -1, maximumValue = 1)
        val deltaY = (y - horizontalMiddlePoint).coerceIn(minimumValue = -1, maximumValue = 1)

        return when (wallRenderTile) {
            ObjectRenderTile.Wall15 -> (this - calcFilterForWall15(deltaX = deltaX, deltaY = deltaY)).toWallRenderTile()
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

    private fun ObjectRenderTile.isWall(): Boolean = when (this) {
        ObjectRenderTile.Wall0,
        ObjectRenderTile.Wall1,
        ObjectRenderTile.Wall2,
        ObjectRenderTile.Wall3,
        ObjectRenderTile.Wall4,
        ObjectRenderTile.Wall5,
        ObjectRenderTile.Wall6,
        ObjectRenderTile.Wall7,
        ObjectRenderTile.Wall8,
        ObjectRenderTile.Wall9,
        ObjectRenderTile.Wall10,
        ObjectRenderTile.Wall11,
        ObjectRenderTile.Wall12,
        ObjectRenderTile.Wall13,
        ObjectRenderTile.Wall14,
        ObjectRenderTile.Wall15 -> true
        else -> false
    }

}
