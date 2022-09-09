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
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.new_models.room.Room
import ru.meatgames.tomb.new_models.room.RoomRepo
import ru.meatgames.tomb.util.asIntOffset
import ru.meatgames.tomb.util.asIntSize
import kotlin.math.max

@Preview
@Composable
private fun MapPreview() {
    val context = LocalContext.current
    GameDataProvider.init(context)
    NewAssets.loadAssets(context)
    val roomRepo = RoomRepo(context)
    Map(roomRepo.rooms[0])
}

@Composable
private fun Map(
    room: Room,
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
        val maxSize = max(room.width, room.height)
        val tileDimension = size.width.toInt() / maxSize
        val offset = (size.width.toInt() - (tileDimension * maxSize)) / 2
        val tileSize = IntSize(tileDimension, tileDimension)

        for (i in 0 until room.width * room.height) {
            val x = i % room.width
            val y = i / room.width
            val dstOffset = IntOffset(offset + x * tileDimension, y * tileDimension)
            room.floorTiles[room.floor[i]]?.let { floorTile ->
                drawImage(
                    NewAssets.tileset,
                    srcOffset = floorTile.imageRect.asIntOffset(),
                    srcSize = floorTile.imageRect.asIntSize(),
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
            }
            if (renderType == RoomRenderType.Floor) continue
            room.objectTiles[room.objects[i]]?.let { objectTile ->
                if (objectTile.name == "nothing" || objectTile.name == "void") return@let
                drawImage(
                    NewAssets.tileset,
                    srcOffset = objectTile.imageRect.asIntOffset(),
                    srcSize = objectTile.imageRect.asIntSize(),
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                    alpha = objectAlpha,
                )
            }
        }
    }
}

private enum class RoomRenderType {
    Floor,
    TransparentObjects,
    Full,
}