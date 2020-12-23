package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import ru.meatgames.tomb.*
import ru.meatgames.tomb.new_models.getFloorImage
import ru.meatgames.tomb.screen.compose.game.GameMapChunk
import ru.meatgames.tomb.screen.compose.game.GameMapTile
import ru.meatgames.tomb.screen.compose.game.GameScreenViewModel

@Composable
fun GameScreen(
    gameScreenViewModel: GameScreenViewModel,
    navController: NavController
) {
    val visibleMapChunk = gameScreenViewModel.visibleMapChunk.observeAsState()
    visibleMapChunk.value?.let { Map(it, navController) }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun Map(
    gameMapChunk: GameMapChunk,
    navController: NavController
) = Box(
    modifier = Modifier.background(Color(0x212121)).fillMaxSize()
) {
    LazyGrid(
        items = gameMapChunk.gameMapTiles,
        columns = gameMapChunk.width,
    ) {

    }
    Text(
        text = "Yet Another\nRoguelike",
        modifier = Modifier.padding(16.dp).align(Alignment.Center),
        style = TextStyle(
            fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
    MainMenuButton(
        title = "Новая игра",
        modifier = Modifier.align(Alignment.BottomStart)
    ) {
        navController.navigate(GameState.Stub.id)
    }
    MainMenuButton(
        title = "Выход",
        modifier = Modifier.align(Alignment.BottomEnd)
    ) {
        navController.navigate(GameState.Stub.id)
    }
}

@Composable
private fun MapTile(
    tileIndex: Int,
    rowSize: Int,
    tileRow: Int,
    tile: GameMapTile
) = Canvas(
    modifier = Modifier.background(Color(0x212121)).fillMaxSize()
) {
    if (!tile.isVisible) return@Canvas
    tile.floor?.let { floorTile ->
        drawImage(
            imageResource(id = floorTile.getFloorImage()),
            Offset()
        )
        drawBitmap(Assets.tileset,
            floorTile.imageRect,
            mTileBuffer2[index],
            bitmapPaint)
    }
    tile.objectTile?.let { objectTile ->
        canvas.drawBitmap(Assets.tileset,
            objectTile.imageRect,
            mTileBuffer2[index],
            bitmapPaint)
    }
    tile.mShadowPaint?.let {
        canvas.drawRect(
            mTileBuffer2[index].left.toFloat(),
            mTileBuffer2[index].top.toFloat(),
            mTileBuffer2[index].right.toFloat(),
            mTileBuffer2[index].bottom.toFloat(),
            it)
    }
}