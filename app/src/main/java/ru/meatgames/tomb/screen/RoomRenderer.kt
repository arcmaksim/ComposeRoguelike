package ru.meatgames.tomb.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.render.RoomPreviewRenderProcessor
import ru.meatgames.tomb.model.room.data.RoomsRepository
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.render.WallsDecorator
import ru.meatgames.tomb.domain.map.MapTile
import ru.meatgames.tomb.domain.map.MapTileWrapper
import ru.meatgames.tomb.model.theme.ASSETS_TILE_DIMENSION
import ru.meatgames.tomb.model.theme.ASSETS_TILE_SIZE
import kotlin.math.max

@Preview
@Composable
private fun RoomRenderer() {
    val context = LocalContext.current

    val roomsData = RoomsRepository(context).loadData()
    val mapRenderProcessor = RoomPreviewRenderProcessor(
        themeAssets = ThemeAssets(context),
        mapDecorators = setOf(WallsDecorator()),
    )

    val room = roomsData.rooms.random()

    val mapTiles = (0 until room.width * room.height).map { index ->
        MapTileWrapper(
            x = index % room.width,
            y = index % room.height,
            tile = MapTile(
                floorEntityTile = roomsData.floorMapping
                    .first { it.symbol == room.floor[index].toString() }
                    .entity,
                objectEntityTile = roomsData.objectMapping
                    .first { it.symbol == room.objects[index].toString() }
                    .let { it.entity },
            ),
        )
    }

    val renderTiles = mapRenderProcessor.produceRenderTilesFrom(
        tiles = mapTiles,
        tilesLineWidth = room.width,
    ).map { it.second }

    val roomPreviewData = RoomPreviewData(
        roomName = room.name,
        roomWidth = room.width,
        roomHeight = room.height,
        tiles = renderTiles,
        outerWalls = room.outerWalls,
    )

    RoomRenderer(
        roomPreviewData = roomPreviewData,
    )
}

@Composable
private fun RoomRenderer(
    roomPreviewData: RoomPreviewData,
    renderType: RoomRenderType = RoomRenderType.Full,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    val objectAlpha = if (renderType == RoomRenderType.TransparentObjects) .2f else 1f
    
    val bitmap = Bitmap.createBitmap(
        ASSETS_TILE_DIMENSION,
        ASSETS_TILE_DIMENSION,
        Bitmap.Config.RGB_565,
    ).run {
        eraseColor(android.graphics.Color.MAGENTA)
        asImageBitmap()
    }
    
    Text(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 48.dp)
            .padding(horizontal = 24.dp),
        text = roomPreviewData.roomName,
        style = h2TextStyle,
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center),
    ) {
        val maxSize = max(roomPreviewData.roomWidth, roomPreviewData.roomHeight)
        val tileDimension = size.width.toInt() / maxSize
        val outlineOffset = (size.width.toInt() - (tileDimension * maxSize)) / 2
        
        val horizontalOffset = if (roomPreviewData.roomWidth < roomPreviewData.roomHeight) {
            ((roomPreviewData.roomHeight - roomPreviewData.roomWidth) / 2f * tileDimension).toInt()
        } else {
            0
        }
        val verticalOffset = if (roomPreviewData.roomHeight < roomPreviewData.roomWidth) {
            ((roomPreviewData.roomWidth - roomPreviewData.roomHeight) / 2f * tileDimension).toInt()
        } else {
            0
        }
        
        val tileSize = IntSize(tileDimension, tileDimension)

        roomPreviewData.tiles.mapIndexed { index, renderTile ->
            if (renderTile is MapRenderTile.Content) {
                val column = index % roomPreviewData.roomWidth
                val row = index / roomPreviewData.roomWidth
                val dstOffset = IntOffset(
                    outlineOffset + horizontalOffset + column * tileDimension,
                    verticalOffset + row * tileDimension,
                )

                drawImage(
                    image = renderTile.floorData.asset,
                    srcOffset = renderTile.floorData.offset,
                    srcSize = ASSETS_TILE_SIZE,
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
                renderTile.objectData?.let { objectTile ->
                    drawImage(
                        image = objectTile.asset,
                        srcOffset = objectTile.offset,
                        srcSize = ASSETS_TILE_SIZE,
                        dstOffset = dstOffset,
                        dstSize = tileSize,
                        filterQuality = FilterQuality.None,
                        alpha = objectAlpha,
                    )
                }
                
                if (roomPreviewData.outerWalls.contains(column to row)) {
                    drawImage(
                        image = bitmap,
                        srcSize = ASSETS_TILE_SIZE,
                        dstOffset = dstOffset,
                        dstSize = tileSize,
                        filterQuality = FilterQuality.None,
                        alpha = .4f,
                    )
                }
            }
        }
    }
}

data class RoomPreviewData(
    val roomName: String,
    val roomWidth: Int,
    val roomHeight: Int,
    val tiles: List<MapRenderTile>,
    val outerWalls: Set<Pair<Int, Int>>,
)

private enum class RoomRenderType {
    Floor,
    TransparentObjects,
    Full,
}
