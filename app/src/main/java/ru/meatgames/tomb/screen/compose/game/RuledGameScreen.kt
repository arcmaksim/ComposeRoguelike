package ru.meatgames.tomb.screen.compose.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurpose
import ru.meatgames.tomb.new_models.themed.domain.tile.getOffset
import ru.meatgames.tomb.new_models.themed.domain.tile.getSize
import kotlin.math.abs

@Composable
fun RuledGameScreen(
    gameScreenViewModel: RuledGameScreenViewModel,
    navController: NavController,
) {
    val visibleMapChunk by gameScreenViewModel.visibleMapChunk.collectAsState()
    Map(visibleMapChunk, navController, gameScreenViewModel::onMoveCharacter)
}

@Composable
private fun Map(
    gameMapChunk: ThemedGameMapChunk,
    navController: NavController,
    onCharacterMove: (Direction) -> Unit,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onCharacterMove(
                            when {
                                it.x > it.y && it.x.toDp() > maxWidth - it.y.toDp() -> Direction.Right
                                it.x > it.y -> Direction.Top
                                it.x < it.y && it.y.toDp() > maxWidth - it.x.toDp() -> Direction.Bottom
                                else -> Direction.Left
                            }
                        )
                    }
                )
            },
    ) {
        val tileDimension = size.width.toInt() / gameMapChunk.width
        val offset = (size.width.toInt() - (tileDimension * gameMapChunk.width)) / 2
        val tileSize = IntSize(tileDimension, tileDimension)

        val animation = derivedStateOf {
            (abs(System.currentTimeMillis()) / 600 % 2).toInt()
        }

        gameMapChunk.gameMapTiles.mapIndexed { index, tile ->
            //if (!tile.isVisible) return@Canvas

            val column = index % gameMapChunk.width
            val row = index / gameMapChunk.width
            val dstOffset = IntOffset(offset + column * tileDimension, row * tileDimension)

            tile.floor?.let { floorTile ->
                if (floorTile.purposeDefinition.purpose == ThemedTilePurpose.Empty) return@let
                drawImage(
                    NewAssets.themedTileset,
                    srcOffset = floorTile.getOffset(),
                    srcSize = floorTile.getSize(),
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
            }
            tile.`object`?.let { objectTile ->
                if (objectTile.purposeDefinition.purpose == ThemedTilePurpose.Empty) return@let
                drawImage(
                    NewAssets.themedTileset,
                    srcOffset = objectTile.getOffset(),
                    srcSize = objectTile.getSize(),
                    dstOffset = dstOffset,
                    dstSize = tileSize,
                    filterQuality = FilterQuality.None,
                )
            }
        }

        drawImage(
            NewAssets.getHeroBitmap(animation.value),
            dstOffset = IntOffset(
                x = offset + tileDimension * (themedViewportWidth / 2),
                y = tileDimension * (themedViewportHeight / 2),
            ),
            dstSize = tileSize,
            filterQuality = FilterQuality.None,
        )
    }
}
