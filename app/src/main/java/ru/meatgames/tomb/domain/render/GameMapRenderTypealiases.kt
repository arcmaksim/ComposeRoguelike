package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.presentation.render.MapRenderTile
import ru.meatgames.tomb.domain.map.MapTileWrapper

typealias RenderTiles = Pair<FloorRenderTile, ObjectRenderTile?>
typealias ScreenSpaceRenderTiles = Pair<MapTileWrapper, RenderTiles>
typealias ScreenSpaceMapRenderTile = Pair<MapTileWrapper?, MapRenderTile>
