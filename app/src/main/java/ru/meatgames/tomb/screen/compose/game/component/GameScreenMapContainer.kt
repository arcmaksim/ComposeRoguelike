package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.awaitAll
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.screen.compose.game.GameScreenInteractionController
import ru.meatgames.tomb.screen.compose.game.GameScreenInteractionState
import ru.meatgames.tomb.screen.compose.game.GameScreenNavigator
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize
import ru.meatgames.tomb.screen.compose.game.animation.GameScreenAnimationState
import ru.meatgames.tomb.screen.compose.game.animation.assembleGameScreenAnimations
import ru.meatgames.tomb.screen.compose.game.animation.isStateless
import ru.meatgames.tomb.screen.compose.game.interactionControllerPreviewStub
import ru.meatgames.tomb.screen.compose.game.navigatorPreviewStub
import ru.meatgames.tomb.toIntOffset

const val ANIMATION_FRAMES = 2
private const val CHARACTER_IDLE_ANIMATION_TIME = 600

@Preview
@Composable
private fun GameScreenMapContainerPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    
    GameScreenMapContainer(
        mapState = gameScreenMapContainerPreviewMapReadyState(themeAssets),
        playerAnimation = null,
        interactionState = null,
        previousMoveDirection = null,
        animationTime = 300,
        navigator = navigatorPreviewStub,
        interactionController = interactionControllerPreviewStub,
    )
}

// TODO add interface for container interactions
@Composable
internal fun GameScreenMapContainer(
    mapState: MapScreenController.MapScreenState.Ready,
    playerAnimation: GameScreenAnimationState?,
    interactionState: GameScreenInteractionState?,
    previousMoveDirection: Direction?,
    animationTime: Int,
    navigator: GameScreenNavigator,
    interactionController: GameScreenInteractionController,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize()
) {
    val isPlayerAnimationStateless = playerAnimation.isStateless
    
    val screenWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
    val tileDimension = screenWidth / mapState.viewportWidth
    
    val view = LocalView.current
    val shakeOffset = remember(playerAnimation) { mutableStateOf(IntOffset.Zero) }
    val horizontalOffset = IntOffset(
        x = (screenWidth - (tileDimension * mapState.viewportWidth)) / 2 + shakeOffset.value.x,
        y = 0,
    )
    
    val initialOffset = previousMoveDirection?.toIntOffset(tileDimension) ?: IntOffset.Zero
    val animatedOffset = remember(playerAnimation) { mutableStateOf(IntOffset.Zero) }
    val revealedTilesAlpha = remember(playerAnimation) { mutableStateOf(if (isPlayerAnimationStateless) 0f else 1f) }
    val fadedTilesAlpha = remember(playerAnimation) { mutableStateOf(if (isPlayerAnimationStateless) 1f else 0f) }
    
    val characterIdleTransition = rememberInfiniteTransition()
    val characterAnimationFrame by characterIdleTransition.animateValue(
        initialValue = 0,
        targetValue = ANIMATION_FRAMES,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = CHARACTER_IDLE_ANIMATION_TIME * ANIMATION_FRAMES,
                easing = LinearEasing,
            ),
        )
    )
    
    LaunchedEffect(playerAnimation) {
        awaitAll(
            *playerAnimation.assembleGameScreenAnimations(
                animationTime = animationTime,
                view = view,
                shakeOffset = shakeOffset,
                animatedOffset = animatedOffset,
                initialAnimatedOffset = -initialOffset,
                revealedTilesAlpha = revealedTilesAlpha,
                fadedTilesAlpha = fadedTilesAlpha,
            )
        )
    }
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
        .align(Alignment.Center)
        .offset { shakeOffset.value }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .align(Alignment.Center)
            .background(LocalBackgroundColor.current)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (interactionState == null) {
                            (interactionController::processCharacterMoveInput)(it.toDirection(maxWidth))
                        }
                    },
                )
            },
    )
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(tileDimension, tileDimension),
        LocalHorizontalOffset provides horizontalOffset,
        LocalBackgroundColor provides Color(0xFF212121),
    ) {
        GameScreenMap(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            tilesToReveal = mapState.tilesToReveal,
            tilesToFade = mapState.tilesToFade,
            animatedOffset = animatedOffset.value,
            characterFrameIndex = characterAnimationFrame,
            initialOffset = initialOffset,
            revealedTilesAlpha = revealedTilesAlpha.value,
            fadedTilesAlpha = fadedTilesAlpha.value,
        )
        
        GameScreenCharacter(
            modifier = modifier,
            frameIndex = characterAnimationFrame,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
            characterRenderData = mapState.characterRenderData,
        )
    }
    
    BaseTextButton(
        title = "Character Sheet",
        modifier = Modifier.align(Alignment.TopEnd),
        onClick = navigator::navigateToCharacterSheet,
    )
    
    BaseTextButton(
        title = "Inventory",
        modifier = Modifier.align(Alignment.BottomStart),
        onClick = navigator::navigateToInventory,
    )
    
    BaseTextButton(
        title = "New map",
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = navigator::onNewMapRequest,
    )
    
    interactionState?.let { state ->
        when (state) {
            is GameScreenInteractionState.SearchingContainer -> {
                GameScreenContainerWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    interactionState = state,
                    onClose = interactionController::closeInteractionMenu,
                    onItemClick = interactionController::itemSelected,
                )
            }
        }
    }
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
