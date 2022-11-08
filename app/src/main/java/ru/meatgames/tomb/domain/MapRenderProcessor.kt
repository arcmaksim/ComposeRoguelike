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
import ru.meatgames.tomb.screen.compose.game.MapTile
import javax.inject.Inject

typealias RenderTiles = Pair<FloorRenderTile, ObjectRenderTile?>

class MapRenderProcessor @Inject constructor(
    private val themeAssets: ThemeAssets,
    private val mapDecorators: Set<@JvmSuppressWildcards MapRenderTilesDecorator>,
) {

    // Assumes tiles is a square
    fun produceRenderTilesFrom(
        tiles: List<MapTile?>,
        tilesLineWidth: Int,
        shouldRenderTile: (Int) -> Boolean,
    ): List<MapRenderTile> = tiles.mapToRenderTiles()
        .applyDecorators(tilesLineWidth)
        .filterExtendedTiles(tilesLineWidth)
        .applyFOV(shouldRenderTile)

    private fun List<MapTile?>.mapToRenderTiles(): List<RenderTiles?> = map {
        it ?: return@map null
        RenderTiles(
            first = it.floorEntityTile.toFloorRenderTile(),
            second = it.objectEntityTile?.toObjectRenderTile(),
        )
    }

    private fun List<RenderTiles?>.applyDecorators(
        tilesLineWidth: Int,
    ): List<RenderTiles?> = run {
        mapDecorators.fold(this) { tiles, decorator ->
            decorator.processMapRenderTiles(tiles, tilesLineWidth)
        }
    }

    private fun List<RenderTiles?>.filterExtendedTiles(
        tilesLineWidth: Int,
    ): List<RenderTiles?> = filterIndexed { index, _ ->
        val x = index % tilesLineWidth
        if (x == 0 || x == tilesLineWidth - 1) return@filterIndexed false

        val y = index / tilesLineWidth
        if (y == 0 || y == tilesLineWidth - 1) return@filterIndexed false

        true
    }

    private fun List<RenderTiles?>.applyFOV(
        shouldRenderTile: (Int) -> Boolean,
    ): List<MapRenderTile> = mapIndexed { index, tile ->
        when {
            tile == null -> MapRenderTile.Hidden

            shouldRenderTile(index) -> MapRenderTile.Revealed(
                floorData = tile.first.toFloorRenderTileData(),
                objectData = tile.second?.toObjectRenderTileData(),
            )

            else -> MapRenderTile.Hidden
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

    private fun ObjectEntityTile.toObjectRenderTile(): ObjectRenderTile = when (this) {
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
