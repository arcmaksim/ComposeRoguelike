package ru.meatgames.tomb.screen.compose.game.component

import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.domain.enemy.produceEnemy
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.RenderData

internal const val gameScreenMapContainerPreviewMapSize = 5

internal fun gameScreenMapContainerPreviewRenderTiles(
    themeAssets: ThemeAssets,
): List<MapRenderTile> {
    val renderTiles = listOf(
        FloorRenderTile.Floor to ObjectRenderTile.Wall6,
        FloorRenderTile.Floor to ObjectRenderTile.Wall10,
        FloorRenderTile.Floor to ObjectRenderTile.Wall8,
        FloorRenderTile.Floor to ObjectRenderTile.DoorOpened,
        FloorRenderTile.Floor to ObjectRenderTile.Wall4,
        
        FloorRenderTile.Floor to ObjectRenderTile.Wall1,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to ObjectRenderTile.Wall5,
        
        FloorRenderTile.Floor to ObjectRenderTile.DoorOpened,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to ObjectRenderTile.StairsUp,
        FloorRenderTile.Floor to ObjectRenderTile.Wall5,
        
        FloorRenderTile.Floor to ObjectRenderTile.Wall4,
        FloorRenderTile.Floor to ObjectRenderTile.StairsDown,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to null,
        FloorRenderTile.Floor to ObjectRenderTile.Wall5,
        
        FloorRenderTile.Floor to ObjectRenderTile.Wall3,
        FloorRenderTile.Floor to ObjectRenderTile.Wall8,
        FloorRenderTile.Floor to ObjectRenderTile.DoorClosed,
        FloorRenderTile.Floor to ObjectRenderTile.Wall2,
        FloorRenderTile.Floor to ObjectRenderTile.Wall9,
    )
    
    val items = mapOf(
        17 to themeAssets.resolveItemRenderData(),
    )
    
    val enemies = mapOf(
        7 to EnemyType.SkeletonWarrior.produceEnemy(0 to 0).let(themeAssets::getEnemyRenderData),
    )
    
    return renderTiles.mapIndexed { index, tilesPair ->
        val floorData = themeAssets.resolveFloorRenderData(tilesPair.first)
        val objectData = tilesPair.second?.let {
            themeAssets.resolveObjectRenderData(it)
        }
        MapRenderTile.Content(
            floorData = RenderData(
                asset = floorData.first,
                offset = floorData.second,
            ),
            objectData = objectData?.let {
                RenderData(
                    asset = it.first,
                    offset = it.second,
                )
            },
            itemData = items[index],
            enemyData = enemies[index],
            isVisible = true,
        )
    }
}

internal fun gameScreenMapContainerPreviewMapReadyState(
    themeAssets: ThemeAssets,
): MapScreenController.MapScreenState.Ready = MapScreenController.MapScreenState.Ready(
    tiles = gameScreenMapContainerPreviewRenderTiles(themeAssets),
    tilesWidth = gameScreenMapContainerPreviewMapSize,
    tilesPadding = 0,
    viewportWidth = gameScreenMapContainerPreviewMapSize,
    viewportHeight = gameScreenMapContainerPreviewMapSize,
    tilesToFade = emptySet(),
    tilesToReveal = emptySet(),
    characterRenderData = themeAssets.characterRenderData,
)
