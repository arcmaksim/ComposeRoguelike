package ru.meatgames.tomb.screen.compose.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerAnimationState
import ru.meatgames.tomb.render.CharacterIdleAnimationDirection
import ru.meatgames.tomb.render.MapRenderTile

private const val HERO_ANIMATION_FRAME_TIME = 600
private const val HERO_ANIMATION_FRAMES = 2

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    onWin: () -> Unit,
) {
    val mapState by viewModel.mapState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            event?.let { onWin() }
        }
    }

    val animation by viewModel.animationState.collectAsState(initial = PlayerAnimationState.NoAnimation)

    when (val state = mapState) {
        is MapScreenController.MapScreenState.Loading -> Loading()
        is MapScreenController.MapScreenState.Ready -> Map(
            mapState = state,
            animation = animation,
            onCharacterMove = viewModel::onMoveCharacter,
            onMapGeneration = viewModel::newMap,
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
    animation: PlayerAnimationState,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize()
) {
    val width = LocalDensity.current.run { maxWidth.toPx() }.toInt()
    val tileDimension = width / mapState.viewportWidth

    val view = LocalView.current
    val shakeHorizontalOffset = remember { Animatable(0f) }
    val horizontalOffset = IntOffset(
        x = (width - (tileDimension * mapState.viewportWidth)) / 2 + shakeHorizontalOffset.value.toInt(),
        y = 0,
    )

    LaunchedEffect(animation) {
        when (animation) {
            is PlayerAnimationState.Shake -> {
                shakeAnimation(
                    view = view,
                    offset = shakeHorizontalOffset,
                )
            }
            else -> Unit
        }
    }
    
    Text(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 16.dp, end = 16.dp),
        text = "${mapState.points}",
        style = h2TextStyle,
    )
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
        .align(Alignment.Center)
        .offset(shakeHorizontalOffset.value.dp, 0.dp)
    
    Box(
        modifier = modifier
            .background(Color(0x1F000000))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onCharacterMove(it.toDirection(size = maxWidth)) }
                )
            },
    )
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(tileDimension, tileDimension),
        LocalHorizontalOffset provides horizontalOffset,
    ) {
        DrawMap(
            modifier = modifier,
            viewportWidth = mapState.viewportWidth,
            tiles = mapState.tiles,
        )
    
        DrawCharacter(
            modifier = modifier,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
        )
    }
    
    BaseTextButton(
        title = "New map",
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = onMapGeneration,
    )
}

context(Density)
private fun Offset.toDirection(
    size: Dp,
): Direction = when {
    x > y && x.toDp() > size - y.toDp() -> Direction.Right
    x > y -> Direction.Top
    x < y && y.toDp() > size - x.toDp() -> Direction.Bottom
    else -> Direction.Left
}

@Composable
private fun DrawMap(
    modifier: Modifier,
    viewportWidth: Int,
    tiles: List<MapRenderTile?>,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    Canvas(modifier = modifier) {
        tiles.forEachIndexed { index, renderTile ->
            val column = index % viewportWidth
            val row = index / viewportWidth
            val dstOffset = offset + IntOffset(
                column * tileDimension,
                row * tileDimension,
            )
            
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
    }
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

@Composable
private fun DrawCharacter(
    modifier: Modifier,
    viewportWidth: Int,
    viewportHeight: Int,
    idleAnimationDirection: CharacterIdleAnimationDirection = CharacterIdleAnimationDirection.Left,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    val infiniteTransition = rememberInfiniteTransition()
    
    val heroAnimationDirectionModifier by remember {
        mutableStateOf(
            when (idleAnimationDirection) {
                CharacterIdleAnimationDirection.Left -> 0
                CharacterIdleAnimationDirection.Right -> 2
            }
        )
    }
    
    val heroAnimationFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = HERO_ANIMATION_FRAMES,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = HERO_ANIMATION_FRAME_TIME * HERO_ANIMATION_FRAMES,
                easing = LinearEasing,
            ),
        )
    )
    
    val resultHeroFrame by remember {
        derivedStateOf {
            heroAnimationDirectionModifier + heroAnimationFrame
        }
    }
    
    Canvas(modifier = modifier) {
        drawImage(
            image = NewAssets.getHeroBitmap(resultHeroFrame),
            dstOffset = offset + IntOffset(
                x = tileDimension * (viewportWidth / 2),
                y = tileDimension * (viewportHeight / 2)
            ),
            dstSize = tileSize,
            filterQuality = FilterQuality.None,
        )
    }
}
