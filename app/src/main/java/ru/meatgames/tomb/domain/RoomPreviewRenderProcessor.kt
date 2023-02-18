package ru.meatgames.tomb.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import ru.meatgames.tomb.render.RenderData
import ru.meatgames.tomb.screen.compose.game.render.RenderTiles
import ru.meatgames.tomb.screen.compose.game.render.ScreenSpaceMapRenderTile
import ru.meatgames.tomb.screen.compose.game.render.ScreenSpaceRenderTiles
import javax.inject.Inject

class RoomPreviewRenderProcessor @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val mapDecorators: Set<@JvmSuppressWildcards MapRenderTilesDecorator>,
) {
    
    // Assumes tiles is a square
    fun produceRenderTilesFrom(
        tiles: List<MapTileWrapper?>,
        tilesLineWidth: Int,
    ): List<ScreenSpaceMapRenderTile> = tiles.mapToRenderTiles()
        .applyDecorators(tilesLineWidth)
        .revealAllTiles()
    
    private fun List<MapTileWrapper?>.mapToRenderTiles(): List<ScreenSpaceRenderTiles?> = map {
        it ?: return@map null
        it to RenderTiles(
            first = it.tile.floorEntityTile.toFloorRenderTile(),
            second = (it.tile.mapObject as? MapTile.MapObject.Object)?.objectEntityTile?.toObjectRenderTile(),
        )
    }
    
    private fun List<ScreenSpaceRenderTiles?>.applyDecorators(
        tilesLineWidth: Int,
    ): List<ScreenSpaceRenderTiles?> = run {
        mapDecorators.fold(this) { tiles, decorator ->
            decorator.processMapRenderTiles(tiles, tilesLineWidth)
        }
    }
    
    private fun List<ScreenSpaceRenderTiles?>.revealAllTiles(): List<ScreenSpaceMapRenderTile> = map { pair ->
        when (pair) {
            null -> null to MapRenderTile.Empty
            else -> pair.first to MapRenderTile.Content(
                floorData = pair.second.first.toFloorRenderTileData(),
                objectData = pair.second.second?.toObjectRenderTileData(),
                isVisible = true,
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
            srcOffset = second,
        )
    
}