package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.domain.MapRenderProcessor
import ru.meatgames.tomb.render.WallsDecorator
import ru.meatgames.tomb.model.room.data.RoomsRepository
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.MapTile
import kotlin.math.max

@Preview
@Composable
private fun RoomRenderer() {
    val context = LocalContext.current

    val roomsData = RoomsRepository(context).loadData()
    val mapRenderProcessor = MapRenderProcessor(
        themeAssets = ThemeAssets(context),
        mapDecorators = setOf(WallsDecorator()),
    )

    val room = roomsData.rooms.first()

    val mapTiles = (0 until room.width * room.height).map { index ->
        MapTile(
            floorEntityTile = roomsData.floorMapping
                .first { it.symbol == room.floor[index].toString() }.entity,
            objectEntityTile = roomsData.objectMapping
                .first { it.symbol == room.objects[index].toString() }.entity,
        )
    }

    val renderTiles = mapRenderProcessor.produceRenderTilesFrom(
        tiles = mapTiles,
        tilesLineWidth = room.width,
        shouldRenderTile = { true },
    )

    val roomPreviewData = RoomPreviewData(
        roomName = room.name,
        roomWidth = room.width,
        roomHeight = room.height,
        tiles = renderTiles,
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

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center),
    ) {
        val maxSize = max(roomPreviewData.roomWidth, roomPreviewData.roomHeight)
        val tileDimension = size.width.toInt() / maxSize
        val offset = (size.width.toInt() - (tileDimension * maxSize)) / 2
        val tileSize = IntSize(tileDimension, tileDimension)

        roomPreviewData.tiles.mapIndexed { index, renderTile ->
            if (renderTile is MapRenderTile.Revealed) {
                val column = index % roomPreviewData.roomWidth
                val row = index / roomPreviewData.roomWidth
                val dstOffset = IntOffset(offset + column * tileDimension, row * tileDimension)

                drawImage(
                    image = renderTile.floorData.asset,
                    srcOffset = renderTile.floorData.srcOffset,
                    srcSize = NewAssets.tileSize,
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
                renderTile.objectData?.let { objectTile ->
                    drawImage(
                        image = objectTile.asset,
                        srcOffset = objectTile.srcOffset,
                        srcSize = NewAssets.tileSize,
                        dstOffset = dstOffset,
                        dstSize = tileSize,
                        filterQuality = FilterQuality.None,
                        alpha = objectAlpha,
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
)

private enum class RoomRenderType {
    Floor,
    TransparentObjects,
    Full,
}
