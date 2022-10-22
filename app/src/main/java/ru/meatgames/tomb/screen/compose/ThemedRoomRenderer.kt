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
import ru.meatgames.tomb.model.provider.GameDataProvider
import ru.meatgames.tomb.model.room.data.ThemedRoomsRepository
import ru.meatgames.tomb.model.room.domain.ThemedRoom
import ru.meatgames.tomb.model.room.domain.ThemedRoomSymbolMapping
import ru.meatgames.tomb.model.tile.domain.ThemedTilePurposeDefinition
import ru.meatgames.tomb.model.tile.domain.ThemedTileset
import ru.meatgames.tomb.model.tile.domain.getOffset
import ru.meatgames.tomb.model.tile.domain.getSize
import ru.meatgames.tomb.model.tile.domain.isEmpty
import ru.meatgames.tomb.model.tile.domain.toThemedTile
import kotlin.math.max
import kotlin.random.Random

@Preview
@Composable
private fun ThemedRoomRenderer() {
    val context = LocalContext.current

    GameDataProvider.init(context)
    NewAssets.loadAssets(context)

    val data = ThemedRoomsRepository(context).loadData()

    ThemedRoomRenderer(
        tileset = data.tilesets.random(Random),
        tiles = data.tiles,
        symbolMapping = data.symbolMappings,
        room = data.rooms.first(),
    )
}

@Composable
private fun ThemedRoomRenderer(
    tileset: ThemedTileset,
    tiles: List<ThemedTilePurposeDefinition>,
    symbolMapping: List<ThemedRoomSymbolMapping>,
    room: ThemedRoom,
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

            room.floor[i].toThemedTile(
                tileset = tileset,
                tiles = tiles,
                symbolMapping = symbolMapping,
            ).apply {
                if (purposeDefinition.isEmpty) return@apply
                drawImage(
                    image = NewAssets.themedTileset,
                    srcOffset = getOffset(),
                    srcSize = getSize(),
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
            }

            if (renderType == RoomRenderType.Floor) continue
            room.objects[i].toThemedTile(
                tileset = tileset,
                tiles = tiles,
                symbolMapping = symbolMapping,
            ).apply {
                if (purposeDefinition.isEmpty) return@apply
                drawImage(
                    image = NewAssets.themedTileset,
                    srcOffset = getOffset(),
                    srcSize = getSize(),
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
