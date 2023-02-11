package ru.meatgames.tomb.domain

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import ru.meatgames.tomb.screen.compose.game.MapTileWrapper
import javax.inject.Inject

class MapDecoratorPipeline @Inject constructor(
    private val mapDecorators: Set<@JvmSuppressWildcards MapRenderTilesDecorator>,
) {

    // Assumes tiles is a square
    fun produceRenderTilesFrom(
        tiles: List<MapTileWrapper?>,
        tilesLineWidth: Int,
    ): List<RenderTiles?> = tiles.mapToRenderTiles()
        .applyDecorators(tilesLineWidth)

    private fun List<MapTileWrapper?>.mapToRenderTiles(): List<RenderTiles?> = map {
        val tile = it?.tile ?: return@map null
        RenderTiles(
            first = tile.floorEntityTile.toFloorRenderTile(),
            second = tile.objectEntityTile?.toObjectRenderTile(),
        )
    }
    
    private fun List<RenderTiles?>.applyDecorators(
        tilesLineWidth: Int,
    ): List<RenderTiles?> = run {
        mapDecorators.fold(this) { tiles, decorator ->
            decorator.processMapRenderTiles(tiles, tilesLineWidth)
        }
    }

    private fun FloorEntityTile.toFloorRenderTile(): FloorRenderTile = when (this) {
        FloorEntityTile.Floor -> FloorRenderTile.Floor
    }

    private fun ObjectEntityTile.toObjectRenderTile(): ObjectRenderTile = when (this) {
        ObjectEntityTile.DoorClosed -> ObjectRenderTile.DoorClosed
        ObjectEntityTile.DoorOpened -> ObjectRenderTile.DoorOpened
        ObjectEntityTile.StairsDown -> ObjectRenderTile.StairsDown
        ObjectEntityTile.StairsUp -> ObjectRenderTile.StairsUp
        ObjectEntityTile.Gismo -> ObjectRenderTile.Gismo
        ObjectEntityTile.Wall -> ObjectRenderTile.Wall0
    }

}
