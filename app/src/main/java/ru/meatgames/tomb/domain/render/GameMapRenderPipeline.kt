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
import ru.meatgames.tomb.model.theme.ASSETS_TILE_SIZE
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject

class GameMapRenderPipeline @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val decoratorsPipeline: MapDecoratorPipeline,
    private val itemsHolder: ItemsHolder,
    private val enemiesHolder: EnemiesHolder,
    private val bufferHolderFactory: BufferHolderFactory,
) {

    private var previousVisibleTiles = setOf<Coordinates>()

    fun run(): GameMapPipelineRenderData {
        val bufferHolder = bufferHolderFactory.cachedBufferHolder

        decoratorsPipeline.produceRenderTilesFrom()

        bufferHolder.resolveResultRenderingBuffer()

        val tilesToReveal = mutableSetOf<ScreenSpaceCoordinates>()
        val tilesToFade = mutableSetOf<ScreenSpaceCoordinates>()
        val previousTiles = mutableSetOf<Coordinates>()

        bufferHolder.resultRenderingBuffer.forEachIndexed { index, mapRenderTile ->
            if (mapRenderTile !is MapRenderTile.Content) return@forEachIndexed

            val x = index % bufferHolder.width
            val y = index / bufferHolder.width
            val coordinates =
                (bufferHolder.horizontalOffset + x) to (bufferHolder.verticalOffset + y)

            val previousTileWasVisible = previousVisibleTiles.contains(coordinates)
            val currentTileVisible = mapRenderTile.isVisible

            if (previousTileWasVisible && !currentTileVisible) {
                tilesToFade.add(coordinates)
            }
            if (!previousTileWasVisible && currentTileVisible) {
                tilesToReveal.add(coordinates)
            }

            if (currentTileVisible) {
                previousTiles.add(coordinates)
            }
        }

        previousVisibleTiles = previousTiles

        return GameMapPipelineRenderData(
            tiles = bufferHolder.resultRenderingBuffer.toList(),
            tilesToFadeIn = tilesToReveal.toList(),
            tilesToFadeOut = tilesToFade.toList(),
        )
    }

    private fun BufferHolder.resolveResultRenderingBuffer() {
        for (index in 0 until width * height) {
            val x = index % width
            val y = index / width

            val floorRenderTile = floorRenderingBuffer[index]
            val objectRenderTile = objectRenderingBuffer[index]

            if (floorRenderTile == null) {
                resultRenderingBuffer[index] = MapRenderTile.Empty
                continue
            }

            val coordinates = (horizontalOffset + x) to (verticalOffset + y)

            val enemy = enemiesHolder.getEnemy(coordinates)
            val objectAbove = mapBuffer.getOrNull(index - width)?.objectEntityTile

            resultRenderingBuffer[index] = MapRenderTile.Content(
                floorData = floorRenderTile.toFloorRenderTileData(),
                objectData = objectRenderTile?.toObjectRenderTileData(),
                itemData = itemsHolder.getItemContainer(coordinates)
                    ?.let { themeAssets.resolveItemRenderData() },
                enemyData = enemy?.let { themeAssets.getEnemyRenderData(it) },
                isVisible = visibilityBuffer[index],
                decorations = objectAbove?.takeIf { it.hasBottomShadow() == true }
                    ?.let { listOf(themeAssets.resolveBottomShadow()) }
                    ?: emptyList(),
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
            offset = second,
            size = ASSETS_TILE_SIZE,
        )
    
}

internal fun ObjectEntityTile.hasBottomShadow(): Boolean = when (this) {
    ObjectEntityTile.DoorClosed,
    ObjectEntityTile.StairsUp,
    ObjectEntityTile.Wall -> true
    else -> false
}
