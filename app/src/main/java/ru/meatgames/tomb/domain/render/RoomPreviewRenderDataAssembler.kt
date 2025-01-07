package ru.meatgames.tomb.domain.render

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.domain.map.MapTile
import ru.meatgames.tomb.model.theme.ASSETS_TILE_SIZE
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import ru.meatgames.tomb.render.RenderData

class RoomPreviewRenderDataAssembler(
    private val themeAssets: ThemeAssets,
    private val mapDecorators: Set<@JvmSuppressWildcards MapRenderTilesDecorator>,
    private val bufferHolder: BufferHolder,
) {

    fun run(
        tiles: List<MapTile>,
    ) {
        tiles.forEachIndexed { index, tile ->
            bufferHolder.mapBuffer[index] = tile
            bufferHolder.floorRenderingBuffer[index] = tile.floorEntityTile.toFloorRenderTile()
            bufferHolder.objectRenderingBuffer[index] = tile.objectEntityTile?.toObjectRenderTile()
        }

        mapDecorators.forEach { decorator ->
            decorator.apply()
        }

        bufferHolder.visibilityBuffer.fill(true)
        bufferHolder.resolveResultRenderingBuffer()
    }

    private fun BufferHolder.resolveResultRenderingBuffer() {
        for (index in 0 until width * height) {
            val floorRenderTile = floorRenderingBuffer[index]
            val objectRenderTile = objectRenderingBuffer[index]

            if (floorRenderTile == null) {
                resultRenderingBuffer[index] = MapRenderTile.Empty
                continue
            }

            val objectAbove = mapBuffer.getOrNull(index - width)?.objectEntityTile

            resultRenderingBuffer[index] = MapRenderTile.Content(
                floorData = floorRenderTile.toFloorRenderTileData(),
                objectData = objectRenderTile?.toObjectRenderTileData(),
                itemData = null,
                enemyData = null,
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
    
    private fun FloorEntityTile.toFloorRenderTile(): FloorRenderTile = when (this) {
        FloorEntityTile.Floor -> FloorRenderTile.Floor
    }
    
    private fun ObjectEntityTile.toObjectRenderTile(): ObjectRenderTile? = when (this) {
        ObjectEntityTile.DoorClosed -> ObjectRenderTile.DoorClosed
        ObjectEntityTile.DoorOpened -> ObjectRenderTile.DoorOpened
        ObjectEntityTile.StairsDown -> ObjectRenderTile.StairsDown
        ObjectEntityTile.StairsUp -> ObjectRenderTile.StairsUp
        ObjectEntityTile.Wall -> ObjectRenderTile.Wall0
    }
    
    private fun Pair<ImageBitmap, IntOffset>.toMapRenderData(): RenderData =
        RenderData(
            asset = first,
            offset = second,
            size = ASSETS_TILE_SIZE,
        )
    
}