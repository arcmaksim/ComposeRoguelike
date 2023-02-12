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
typealias ScreenSpaceMapRenderTile = Pair<MapTileWrapper?, MapRenderTile>

class MapRenderProcessor @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val decoratorsPipeline: MapDecoratorPipeline,
) {
    
    private var prevTiles = mutableSetOf<Coordinates>()
    
    // Assumes tiles is a square
    fun runPipeline(
        tiles: List<MapTileWrapper?>,
        tilesLineWidth: Int,
        mapX: Int,
        mapY: Int,
        shouldRenderTile: (Int) -> Boolean,
    ): PipelineRenderData {
        val renderTiles = decoratorsPipeline.produceRenderTilesFrom(
            tiles = tiles,
            tilesLineWidth = tilesLineWidth,
        )
        
        val processedRenderTiles = renderTiles.applyFOV(tilesLineWidth, shouldRenderTile)
        
        val mapRenderTiles = processedRenderTiles.map { it.second }
        
        val newTiles = mutableSetOf<ScreenSpaceCoordinates>()
        val oldTiles = mutableSetOf<ScreenSpaceCoordinates>()
        
        fun MapTileWrapper?.toCoords() = this?.let { it.x to it.y }
        
        processedRenderTiles.forEach { mapRenderTile ->
            val tileWrapper = mapRenderTile.first ?: return@forEach
            val newRenderTile = mapRenderTile.second
            
            val previousRenderTileWasVisible = prevTiles.contains(tileWrapper.toCoords())
            
            fun MapRenderTile?.isVisible() = this is MapRenderTile.Content && isVisible
            
            if (previousRenderTileWasVisible && !newRenderTile.isVisible()) {
                oldTiles.add(tileWrapper.x - mapX to tileWrapper.y - mapY)
            }
            if (!previousRenderTileWasVisible && newRenderTile.isVisible()) {
                newTiles.add(tileWrapper.x - mapX to tileWrapper.y - mapY)
            }
        }
        
        prevTiles.clear()
        prevTiles.addAll(
            processedRenderTiles.filter { (it.second as? MapRenderTile.Content)?.isVisible == true }
                .map { it.first.toCoords()!! }
        )
        
        return PipelineRenderData(
            tiles = mapRenderTiles,
            newTiles = newTiles.toList(),
            exitTiles = oldTiles.toList(),
        )
    }
    
    private fun List<ScreenSpaceRenderTiles?>.applyFOV(
        tilesLineWidth: Int,
        shouldRenderTile: (Int) -> Boolean,
    ): List<ScreenSpaceMapRenderTile> = mapIndexed { index, pair ->
        when (pair) {
            null -> null to MapRenderTile.Empty
            else -> {
                val x = index % tilesLineWidth
                val y = index / tilesLineWidth
                
                val isVisible = if (x == 0 || x == tilesLineWidth - 1 || y == 0 || y == tilesLineWidth - 1) {
                    false
                } else {
                    shouldRenderTile((y - 1) * (tilesLineWidth - 2) + x - 1)
                }
                
                pair.first to MapRenderTile.Content(
                    floorData = pair.second.first.toFloorRenderTileData(),
                    objectData = pair.second.second?.toObjectRenderTileData(),
                    isVisible = isVisible,
                )
            }
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
