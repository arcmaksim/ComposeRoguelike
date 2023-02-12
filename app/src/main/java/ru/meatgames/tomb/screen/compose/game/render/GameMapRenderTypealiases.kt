package ru.meatgames.tomb.screen.compose.game.render

import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.domain.MapTileWrapper

typealias RenderTiles = Pair<FloorRenderTile, ObjectRenderTile?>
typealias ScreenSpaceRenderTiles = Pair<MapTileWrapper, RenderTiles>
typealias ScreenSpaceMapRenderTile = Pair<MapTileWrapper?, MapRenderTile>
