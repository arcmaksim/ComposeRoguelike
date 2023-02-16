package ru.meatgames.tomb.screen.compose.game

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.awaitAll
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerAnimationState
import ru.meatgames.tomb.domain.ScreenSpaceCoordinates
import ru.meatgames.tomb.domain.isStateless
import ru.meatgames.tomb.render.CharacterIdleAnimationDirection
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.animation.assembleGameScreenAnimations
import ru.meatgames.tomb.toIntOffset

private const val HERO_ANIMATION_FRAME_TIME = 600
private const val HERO_ANIMATION_FRAMES = 2
private const val HERO_MOVE_ANIMATION_TIME = 300

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    onWin: () -> Unit,
    onInventory: () -> Unit,
    onExit: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                GameScreenEvent.Win -> onWin()
                GameScreenEvent.Inventory -> onInventory()
                else -> Unit
            }
        }
    }
    
    BackHandler(onBack = onExit)
    
    val gameScreenState by viewModel.state.collectAsState(GameScreenState())
    
    when (val mapState = gameScreenState.mapState) {
        is MapScreenController.MapScreenState.Loading -> MapLoading()
        is MapScreenController.MapScreenState.Ready -> RenderMap(
            mapState = mapState,
            playerAnimation = gameScreenState.playerAnimation,
            previousMoveDirection = gameScreenState.previousMoveDirection,
            onCharacterMove = viewModel::onMoveCharacter,
            onMapGeneration = viewModel::newMap,
            onInventory = viewModel::openInventory,
        )
    }
}

@Composable
private fun MapLoading() {
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
private fun RenderMap(
    mapState: MapScreenController.MapScreenState.Ready,
    playerAnimation: PlayerAnimationState?,
    previousMoveDirection: Direction?,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
    onInventory: () -> Unit,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize()
) {
    val isPlayerAnimationStateless = playerAnimation.isStateless
    
    val screenWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
    val tileDimension = screenWidth / mapState.viewportWidth
    
    val view = LocalView.current
    val shakeHorizontalOffset = remember(playerAnimation) { mutableStateOf(0f) }
    val horizontalOffset = IntOffset(
        x = (screenWidth - (tileDimension * mapState.viewportWidth)) / 2 + shakeHorizontalOffset.value.toInt(),
        y = 0,
    )
    
    val initialOffset = previousMoveDirection?.toIntOffset(tileDimension) ?: IntOffset.Zero
    val animatedOffset = remember(playerAnimation) { mutableStateOf(IntOffset.Zero) }
    val revealedTilesAlpha = remember(playerAnimation) { mutableStateOf(if (isPlayerAnimationStateless) 0f else 1f) }
    val fadedTilesAlpha = remember(playerAnimation) { mutableStateOf(if (isPlayerAnimationStateless) 1f else 0f) }
    
    LaunchedEffect(playerAnimation) {
        awaitAll(
            *playerAnimation.assembleGameScreenAnimations(
                animationTime = HERO_MOVE_ANIMATION_TIME,
                view = view,
                shakeHorizontalOffset = shakeHorizontalOffset,
                animatedOffset = animatedOffset,
                initialAnimatedOffset = -initialOffset,
                revealedTilesAlpha = revealedTilesAlpha,
                fadedTilesAlpha = fadedTilesAlpha,
            )
        )
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
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center)
            .background(LocalBackgroundColor.current)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onCharacterMove(it.toDirection(maxWidth)) },
                )
            },
    )
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(tileDimension, tileDimension),
        LocalHorizontalOffset provides horizontalOffset,
        LocalBackgroundColor provides Color(0xFF212121),
    ) {
        DrawMap(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            tilesToReveal = mapState.tilesToReveal,
            tilesToFade = mapState.tilesToFade,
            animatedOffset = animatedOffset.value,
            initialOffset = initialOffset,
            revealedTilesAlpha = revealedTilesAlpha.value,
            fadedTilesAlpha = fadedTilesAlpha.value,
        )
        
        DrawCharacter(
            modifier = modifier,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
        )
    }
    
    BaseTextButton(
        title = "Inventory",
        modifier = Modifier.align(Alignment.BottomStart),
        onClick = onInventory,
    )
    
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
    tiles: List<MapRenderTile?>,
    tilesWidth: Int,
    tilesPadding: Int,
    tilesToReveal: Set<ScreenSpaceCoordinates>,
    tilesToFade: Set<ScreenSpaceCoordinates>,
    initialOffset: IntOffset,
    animatedOffset: IntOffset,
    revealedTilesAlpha: Float,
    fadedTilesAlpha: Float,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    val backgroundColor = LocalBackgroundColor.current
    
    Canvas(modifier = modifier) {
        tiles.forEachIndexed { index, renderTile ->
            val column = index % tilesWidth - tilesPadding
            val row = index / tilesWidth - tilesPadding
            val dstOffset = offset + initialOffset + animatedOffset + IntOffset(
                column * tileDimension,
                row * tileDimension,
            )
            
            val tileScreenSpaceCoordinates = column to row
            
            if (renderTile is MapRenderTile.Content && renderTile.isVisible) {
                val alpha = revealedTilesAlpha.takeIf { tilesToReveal.contains(tileScreenSpaceCoordinates) }
                drawRevealedTile(
                    renderTile = renderTile,
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    alpha = alpha,
                    backgroundColor = backgroundColor,
                )
            }
            if (renderTile is MapRenderTile.Content && !renderTile.isVisible
                && tilesToFade.contains(tileScreenSpaceCoordinates)
            ) {
                drawRevealedTile(
                    renderTile = renderTile,
                    dstOffset = dstOffset,
                    tileSize = tileSize,
                    alpha = fadedTilesAlpha,
                    backgroundColor = backgroundColor,
                )
            }
        }
    }
}

private fun DrawScope.drawRevealedTile(
    renderTile: MapRenderTile.Content,
    dstOffset: IntOffset,
    tileSize: IntSize,
    backgroundColor: Color,
    alpha: Float?,
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
    alpha?.let {
        drawRect(
            topLeft = dstOffset.toOffset(),
            color = backgroundColor,
            alpha = it,
            size = tileSize.toSize(),
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
