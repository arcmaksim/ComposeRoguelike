package ru.meatgames.tomb.screen.compose.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.render.MapRenderTile
import kotlin.math.abs

@Composable
fun GameScreen(
    gameScreenViewModel: GameScreenViewModel,
    navController: NavController,
) {
    val mapState by gameScreenViewModel.mapState.collectAsState()

    when (val state = mapState) {
        is MapScreenController.MapScreenState.Loading -> Loading()
        is MapScreenController.MapScreenState.Ready -> Map(
            mapState = state,
            navController = navController,
            onCharacterMove = gameScreenViewModel::onMoveCharacter,
            onMapGeneration = gameScreenViewModel::newMap,
        )
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Loading...",
            style = h2TextStyle,
        )
    }
}

@Composable
private fun Map(
    mapState: MapScreenController.MapScreenState.Ready,
    navController: NavController,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    Text(
        modifier = Modifier.align(Alignment.TopEnd)
            .padding(top = 16.dp, end = 16.dp),
        text = "${mapState.points}",
        style = h2TextStyle,
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center)
            .background(Color(0x1F000000))
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
        val tileDimension = size.width.toInt() / mapState.viewportWidth
        val offset = (size.width.toInt() - (tileDimension * mapState.viewportWidth)) / 2
        val tileSize = IntSize(tileDimension, tileDimension)

        val animation = derivedStateOf {
            (abs(System.currentTimeMillis()) / 600 % 2).toInt()
        }

        mapState.tiles.forEachIndexed { index, renderTile ->
            val column = index % mapState.viewportWidth
            val row = index / mapState.viewportWidth
            val dstOffset = IntOffset(offset + column * tileDimension, row * tileDimension)
            
            when (renderTile) {
                is MapRenderTile.Revealed -> {
                    drawRevealedTile(
                        renderTile = renderTile,
                        dstOffset = dstOffset,
                        tileSize = tileSize,
                    )
                }
                is MapRenderTile.Hidden -> {
                    drawHiddenTile(
                        renderTile = renderTile,
                        dstOffset = dstOffset,
                        tileSize = tileSize,
                    )
                }
                else -> Unit
            }
        }
        drawImage(
            NewAssets.getHeroBitmap(animation.value),
            dstOffset = IntOffset(
                x = offset + tileDimension * (mapState.viewportWidth / 2),
                y = tileDimension * (mapState.viewportHeight / 2)
            ),
            dstSize = tileSize,
            filterQuality = FilterQuality.None,
        )
    }
    
    BaseTextButton(
        title = "New map",
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = onMapGeneration,
    )
}

private fun DrawScope.drawRevealedTile(
    renderTile: MapRenderTile.Revealed,
    dstOffset: IntOffset,
    tileSize: IntSize,
) {
    val filterQuality = FilterQuality.None
    
    drawImage(
        image = renderTile.floorData.asset,
        srcOffset = renderTile.floorData.srcOffset,
        srcSize = NewAssets.tileSize,
        dstOffset = dstOffset,
        dstSize = tileSize,
        filterQuality = filterQuality,
    )
    renderTile.objectData?.let { objectTile ->
        drawImage(
            image = objectTile.asset,
            srcOffset = objectTile.srcOffset,
            srcSize = NewAssets.tileSize,
            dstOffset = dstOffset,
            dstSize = tileSize,
            filterQuality = filterQuality,
        )
    }
}

private fun DrawScope.drawHiddenTile(
    renderTile: MapRenderTile.Hidden,
    dstOffset: IntOffset,
    tileSize: IntSize,
) {
    val filterQuality = FilterQuality.None
    
    renderTile.effectData?.let { effectData ->
        drawImage(
            image = effectData.asset,
            srcOffset = effectData.srcOffset,
            srcSize = NewAssets.tileSize,
            dstOffset = dstOffset,
            dstSize = tileSize,
            filterQuality = filterQuality,
        )
    }
}
