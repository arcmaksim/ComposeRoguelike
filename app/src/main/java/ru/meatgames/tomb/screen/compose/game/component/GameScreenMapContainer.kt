package ru.meatgames.tomb.screen.compose.game.component

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
import ru.meatgames.tomb.NewAssets
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.screen.compose.game.GameScreenInteractionState
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize
import ru.meatgames.tomb.screen.compose.game.animation.GameScreenAnimationState
import ru.meatgames.tomb.screen.compose.game.animation.assembleGameScreenAnimations
import ru.meatgames.tomb.screen.compose.game.animation.isStateless
import ru.meatgames.tomb.toIntOffset

@Preview
@Composable
private fun GameScreenMapContainerPreview() {
    val context = LocalContext.current
    
    NewAssets.loadAssets(context)
    val themeAssets = ThemeAssets(context)
    
    GameScreenMapContainer(
        mapState = gameScreenMapContainerPreviewMapReadyState(themeAssets),
        playerAnimation = null,
        interactionState = null,
        previousMoveDirection = null,
        animationTime = 300,
        characterAnimationFrameTime = 600,
        onCharacterMove = { Unit },
        onMapGeneration = { Unit },
        onInventory = { Unit },
        onCharacterSheet = { Unit },
        onCloseInteractionMenu = { Unit },
        onItemSelected = { _, _, _ -> Unit },
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
    characterAnimationFrameTime: Int,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
    onInventory: () -> Unit,
    onCharacterSheet: () -> Unit,
    onCloseInteractionMenu: () -> Unit,
    onItemSelected: (Coordinates, ItemContainerId, ItemId) -> Unit,
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
                animationTime = animationTime,
                view = view,
                shakeHorizontalOffset = shakeHorizontalOffset,
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
        .offset(shakeHorizontalOffset.value.dp, 0.dp)
    
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
                            onCharacterMove(it.toDirection(maxWidth))
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
            initialOffset = initialOffset,
            revealedTilesAlpha = revealedTilesAlpha.value,
            fadedTilesAlpha = fadedTilesAlpha.value,
        )
        
        GameScreenCharacter(
            modifier = modifier,
            animationFrameTime = characterAnimationFrameTime,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
            characterData = CharacterData.Player,
        )
    }
    
    BaseTextButton(
        title = "Character Sheet",
        modifier = Modifier.align(Alignment.TopEnd),
        onClick = onCharacterSheet,
    )
    
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
    
    interactionState?.let { state ->
        when (state) {
            is GameScreenInteractionState.SearchingContainer -> {
                GameScreenContainerWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    interactionState = state,
                    onClose = onCloseInteractionMenu,
                    onItemClick = onItemSelected,
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
