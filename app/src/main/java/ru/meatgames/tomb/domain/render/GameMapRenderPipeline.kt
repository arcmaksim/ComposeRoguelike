package ru.meatgames.tomb.domain.render

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.enemy.EnemiesHolder
import ru.meatgames.tomb.domain.item.ItemsHolder
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.RenderData
import ru.meatgames.tomb.domain.map.MapTileWrapper
import ru.meatgames.tomb.model.theme.ASSETS_TILE_SIZE
import javax.inject.Inject

class GameMapRenderPipeline @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val decoratorsPipeline: MapDecoratorPipeline,
    private val itemsHolder: ItemsHolder,
    private val enemiesHolder: EnemiesHolder,
) {
    
    private var prevTiles = setOf<Coordinates>()
    
    // Assumes tiles is a square
    fun run(
        tiles: List<MapTileWrapper?>,
        tilesLineWidth: Int,
        startCoordinates: Coordinates,
        shouldRenderTile: (Int) -> Boolean,
    ): GameMapPipelineRenderData {
        val renderTiles = decoratorsPipeline.produceRenderTilesFrom(
            tiles = tiles,
            tilesLineWidth = tilesLineWidth,
        )
        
        val processedRenderTiles = renderTiles.applyFOV(
            tilesLineWidth,
            shouldRenderTile,
        )
        
        val tilesToReveal = mutableSetOf<ScreenSpaceCoordinates>()
        val tilesToFade = mutableSetOf<ScreenSpaceCoordinates>()
        
        fun MapTileWrapper.toCoordinates() = x to y
        
        processedRenderTiles.forEach { mapRenderTile ->
            val newRenderTile = mapRenderTile.second
            val tileWrapper = mapRenderTile.first ?: return@forEach
            
            val previousTileWasVisible = prevTiles.contains(tileWrapper.toCoordinates())
            
            val isCurrentTileVisible = (newRenderTile as? MapRenderTile.Content)?.isVisible ?: false
            
            if (previousTileWasVisible && !isCurrentTileVisible) {
                tilesToFade.add(tileWrapper.x - startCoordinates.first to tileWrapper.y - startCoordinates.second)
            }
            if (!previousTileWasVisible && isCurrentTileVisible) {
                tilesToReveal.add(tileWrapper.x - startCoordinates.first to tileWrapper.y - startCoordinates.second)
            }
        }
        
        prevTiles = processedRenderTiles.filter { it.first != null }
            .filter { (it.second as? MapRenderTile.Content)?.isVisible == true }
            .map { it.first!!.toCoordinates() }
            .toSet()
        
        return GameMapPipelineRenderData(
            tiles = processedRenderTiles.map { it.second },
            tilesToFadeIn = tilesToReveal.toList(),
            tilesToFadeOut = tilesToFade.toList(),
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
                
                // border tiles needs to be skipped as they are not rendered
                val isVisible = if (x == 0 || x == tilesLineWidth - 1 || y == 0 || y == tilesLineWidth - 1) {
                    false
                } else {
                    shouldRenderTile((y - 1) * (tilesLineWidth - 2) + x - 1)
                }
                
                val coordinates = pair.first.x to pair.first.y
                val enemy = enemiesHolder.getEnemy(coordinates)
                val tileAbove = getOrNull(index - tilesLineWidth)?.second
                
                pair.first to MapRenderTile.Content(
                    floorData = pair.second.first.toFloorRenderTileData(),
                    objectData = pair.second.second?.toObjectRenderTileData(),
                    itemData = itemsHolder.getItemContainer(coordinates)
                        ?.let { themeAssets.resolveItemRenderData() },
                    enemyData = enemy?.let { themeAssets.getEnemyRenderData(it) },
                    isVisible = isVisible,
                    decorations = tileAbove?.takeIf { it.second?.hasBottomShadow() == true }
                        ?.let { listOf(themeAssets.resolveBottomShadow()) }
                        ?: emptyList(),
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
            offset = second,
            size = ASSETS_TILE_SIZE,
        )
    
}

internal fun ObjectRenderTile.hasBottomShadow(): Boolean = when (this) {
    ObjectRenderTile.DoorClosed,
    ObjectRenderTile.StairsUp,
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
