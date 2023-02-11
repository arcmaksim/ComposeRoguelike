package ru.meatgames.tomb.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.RenderData
import ru.meatgames.tomb.screen.compose.game.MapTileWrapper
import javax.inject.Inject

typealias RenderTiles = Pair<FloorRenderTile, ObjectRenderTile?>
typealias ScreenSpaceRenderTiles = Pair<MapTileWrapper, RenderTiles>

class MapRenderProcessor @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val decoratorsPipeline: MapDecoratorPipeline,
) {
    
    private var prevTiles: Set<Coordinates> = emptySet()
    
    // Assumes tiles is a square
    fun runPipeline(
        tiles: List<MapTileWrapper?>,
        tilesLineWidth: Int,
        mapX: Int,
        mapY: Int,
        shouldRenderTile: (Int) -> Boolean,
    ): PipelineRenderData {
        // calc new and old
        val coords = tiles.filterNotNull().map { it.x to it.y }.toSet()
        val new = (coords - prevTiles).map { it.first - mapX to it.second - mapY }
        val old = (prevTiles - coords).map { it.first - mapX to it.second - mapY }
        prevTiles = coords
        // run decorators
        val renderTiles = decoratorsPipeline.produceRenderTilesFrom(
            tiles = tiles,
            tilesLineWidth = tilesLineWidth,
        )
        // cull view
        val processedRenderTiles = renderTiles.filterExtendedTiles(tilesLineWidth)
        // calc fov
            .applyFOV(shouldRenderTile)
        
        return PipelineRenderData(
            tiles = processedRenderTiles,
            newTiles = new,
            exitTiles = old,
        )
    }

    private fun List<RenderTiles?>.filterExtendedTiles(
        tilesLineWidth: Int,
    ): List<RenderTiles?> = filterIndexed { index, _ ->
        val x = index % tilesLineWidth
        if (x == 0 || x == tilesLineWidth - 1) return@filterIndexed false

        val y = index / tilesLineWidth
        if (y == 0 || y == (size / tilesLineWidth) - 1) return@filterIndexed false

        true
    }

    private fun List<RenderTiles?>.applyFOV(
        shouldRenderTile: (Int) -> Boolean,
    ): List<MapRenderTile> = mapIndexed { index, tile ->
        when {
            tile == null -> MapRenderTile.Hidden()

            shouldRenderTile(index) -> MapRenderTile.Revealed(
                floorData = tile.first.toFloorRenderTileData(),
                objectData = tile.second?.toObjectRenderTileData(),
            )

            else -> MapRenderTile.Hidden(
                effectData = tile.second
                    ?.takeIf { it == ObjectRenderTile.Gismo }
                    ?.toObjectRenderTileData(),
            )
        }
    }
    
    private fun FloorRenderTile.toFloorRenderTileData(): RenderData =
        themeAssets.resolveFloorRenderData(
            floorRenderTile = this,
        ).toMapRenderData()

    private fun ObjectRenderTile.toObjectRenderTileData(): RenderData =
        themeAssets.resolveObjectRenderData(
            objectRenderTile = this,
        ).toMapRenderData()

    private fun Pair<ImageBitmap, IntOffset>.toMapRenderData(): RenderData =
        RenderData(
            asset = first,
            srcOffset = second,
        )
    
    data class PipelineRenderData(
        val tiles: List<MapRenderTile>,
        val newTiles: List<ScreenSpaceCoordinates>,
        val exitTiles: List<ScreenSpaceCoordinates>,
    )

}
