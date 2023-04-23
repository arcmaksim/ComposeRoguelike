package ru.meatgames.tomb.screen.compose.game.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.awaitAll
import ru.meatgames.tomb.R
import ru.meatgames.tomb.component.IconButton
import ru.meatgames.tomb.component.IllustrationButton
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.domain.map.EnemiesAnimations
import ru.meatgames.tomb.domain.map.MapScreenController
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.screen.compose.game.GameScreenInteractionController
import ru.meatgames.tomb.screen.compose.game.GameScreenNavigator
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize
import ru.meatgames.tomb.domain.player.PlayerInteraction
import ru.meatgames.tomb.screen.compose.game.animation.CHARACTER_IDLE_ANIMATION_DURATION_MILLIS
import ru.meatgames.tomb.screen.compose.game.animation.CHARACTER_IDLE_ANIMATION_FRAMES
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.domain.player.PlayerAnimation
import ru.meatgames.tomb.domain.player.updatesScreenSpaceTiles
import ru.meatgames.tomb.screen.compose.game.animation.EnemyAnimationState
import ru.meatgames.tomb.screen.compose.game.animation.assembleEnemiesAnimations
import ru.meatgames.tomb.screen.compose.game.animation.assemblePlayerInputAnimations
import ru.meatgames.tomb.screen.compose.game.interactionControllerPreviewStub
import ru.meatgames.tomb.screen.compose.game.navigatorPreviewStub
import ru.meatgames.tomb.toDirection
import ru.meatgames.tomb.toIntOffset

@Preview
@Composable
private fun GameScreenMapContainerPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    
    GameScreenMapContainer(
        mapState = gameScreenMapContainerPreviewMapReadyState(themeAssets),
        isIdle = true,
        playerHealth = HealthComponent(10),
        playerAnimation = null,
        enemiesAnimations = emptyList(),
        interactionState = null,
        animationDurationMillis = 300,
        navigator = navigatorPreviewStub,
        interactionController = interactionControllerPreviewStub,
    )
}

@Composable
internal fun GameScreenMapContainer(
    mapState: MapScreenController.MapScreenState.Ready,
    isIdle: Boolean,
    playerHealth: HealthComponent,
    playerAnimation: PlayerAnimation?,
    enemiesAnimations: List<Pair<EnemyId, EnemyAnimation>>?,
    interactionState: PlayerInteraction?,
    animationDurationMillis: Int,
    navigator: GameScreenNavigator,
    interactionController: GameScreenInteractionController,
) = BoxWithConstraints(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    val animationUpdatesScreenSpaceTiles = playerAnimation.updatesScreenSpaceTiles
    
    val screenWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
    val tileDimension = screenWidth / mapState.viewportWidth
    
    val view = LocalView.current
    val shakeOffset = remember(playerAnimation) { mutableStateOf(IntOffset.Zero) }
    val enemiesAnimationUpdates = remember(enemiesAnimations) {
        mutableStateOf(enemiesAnimations?.toMap(tileDimension) ?: emptyMap())
    }
    val horizontalOffset = IntOffset(
        x = (screenWidth - (tileDimension * mapState.viewportWidth)) / 2 + shakeOffset.value.x,
        y = 0,
    )
    
    // Movement offsets
    val initialMovementOffset = (playerAnimation as? PlayerAnimation.Move)?.direction
        ?.toIntOffset(tileDimension)
        ?: IntOffset.Zero
    val animatedMovementOffset = remember(playerAnimation) { mutableStateOf(IntOffset.Zero) }
    
    // Reveal offsets
    val revealedTilesAlpha = remember(playerAnimation) {
        mutableStateOf(if (animationUpdatesScreenSpaceTiles) 0f else 1f)
    }
    val fadedTilesAlpha = remember(playerAnimation) {
        mutableStateOf(if (animationUpdatesScreenSpaceTiles) 1f else 0f)
    }
    
    // Pose animation
    val characterIdleTransition = rememberInfiniteTransition(label = "characterIdleInfiniteTransition")
    val characterAnimationFrameIndex by characterIdleTransition.animateValue(
        initialValue = 0,
        targetValue = CHARACTER_IDLE_ANIMATION_FRAMES,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = CHARACTER_IDLE_ANIMATION_DURATION_MILLIS * CHARACTER_IDLE_ANIMATION_FRAMES,
                easing = LinearEasing,
            ),
        ),
        label = "characterIdleAnimationFrameIndex",
    )
    
    LaunchedEffect(playerAnimation) {
        awaitAll(
            *playerAnimation.assemblePlayerInputAnimations(
                animationDurationMillis = animationDurationMillis,
                view = view,
                shakeOffset = shakeOffset,
                animatedOffset = animatedMovementOffset,
                initialAnimatedOffset = -initialMovementOffset,
                fadeInTilesAlpha = revealedTilesAlpha,
                fadeOutTilesAlpha = fadedTilesAlpha,
            ),
        )
        if (playerAnimation != null) interactionController.finishPlayerAnimation()
    }
    
    LaunchedEffect(enemiesAnimations) {
        when {
            enemiesAnimations == null -> return@LaunchedEffect
            enemiesAnimations.isEmpty() -> {
                interactionController.finishEnemiesAnimation()
                return@LaunchedEffect
            }
            
            else -> {
                awaitAll(
                    *enemiesAnimations.assembleEnemiesAnimations(
                        animationDurationMillis = animationDurationMillis,
                        tileDimension = tileDimension,
                    ) { it, state ->
                        enemiesAnimationUpdates.value = enemiesAnimationUpdates.value.toMutableMap().apply {
                            this[it] = state
                        }
                    },
                )
                interactionController.finishEnemiesAnimation()
            }
        }
    }
    
    val baseModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
        .align(Alignment.Center)
    
    Box(
        modifier = baseModifier
            .background(LocalBackgroundColor.current)
            .pointerInput(isIdle) {
                detectTapGestures(
                    onTap = {
                        if (isIdle && interactionState == null) {
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
        val modifier = baseModifier.offset { shakeOffset.value }
        
        GameScreenMap(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            tilesToReveal = mapState.tilesToFadeIn,
            tilesToFade = mapState.tilesToFadeOut,
            animatedOffset = animatedMovementOffset.value,
            initialOffset = initialMovementOffset,
            revealedTilesAlpha = revealedTilesAlpha.value,
            fadedTilesAlpha = fadedTilesAlpha.value,
        )
        
        GameScreenCharacter(
            modifier = modifier,
            frameIndex = characterAnimationFrameIndex,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
            characterRenderData = mapState.characterRenderData,
        )
        
        GameScreenEnemies(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            tilesToReveal = mapState.tilesToFadeIn,
            tilesToFade = mapState.tilesToFadeOut,
            animationStates = enemiesAnimationUpdates.value,
            animatedOffset = animatedMovementOffset.value,
            initialOffset = initialMovementOffset,
            revealedTilesAlpha = revealedTilesAlpha.value,
            fadedTilesAlpha = fadedTilesAlpha.value,
            characterFrameIndex = characterAnimationFrameIndex,
        )
    }
    
    IconButton(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp),
        iconResId = R.drawable.ic_cog,
        onClick = navigator::showDialog,
    )
    
    BottomControls(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(vertical = 16.dp),
        isIdle = isIdle,
        playerHealth = playerHealth,
        navigator = navigator,
        interactionController = interactionController,
    )
    
    Text(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 16.dp),
        text = if (isIdle) "Play!" else "Wait...",
        style = h3TextStyle,
        color = if (isIdle) Color.White else Color.Red,
    )
    
    interactionState?.let { state ->
        when (state) {
            is PlayerInteraction.SearchingContainer -> {
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

@Composable
private fun BottomControls(
    isIdle: Boolean,
    playerHealth: HealthComponent,
    navigator: GameScreenNavigator,
    interactionController: GameScreenInteractionController,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ),
    ) {
        Stat(
            currentValue = playerHealth.currentHealth.toString(),
            maxValue = playerHealth.maxHealth.toString(),
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IllustrationButton(
                illustrationResId = R.drawable.il_heart,
                onClick = navigator::navigateToCharacterSheet,
            )
            IllustrationButton(
                illustrationResId = R.drawable.il_armor,
                onClick = navigator::navigateToInventory,
            )
            IllustrationButton(
                illustrationResId = R.drawable.il_watch,
                onClick = interactionController::skipTurn,
                enabled = isIdle,
            )
        }
    }
}

@Composable
private fun Stat(
    currentValue: String,
    maxValue: String,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(64.dp)
            .background(
                color = Color.DarkGray,
                shape = shape,
            )
            .clip(shape)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = currentValue,
                style = h1TextStyle,
                color = Color.Red,
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "/$maxValue",
                style = h2TextStyle,
            )
        }
    }
}

private fun EnemiesAnimations.toMap(
    tileDimension: Int,
): Map<EnemyId, EnemyAnimationState?> = associate { (id, animation) ->
    when (animation) {
        is EnemyAnimation.Move -> id to EnemyAnimationState.Transition(
            tileDimension = tileDimension,
            moveState = animation,
        )
        
        is EnemyAnimation.Attack -> id to EnemyAnimationState.Transition(
            offset = IntOffset.Zero,
            alpha = 1f,
        )
        
        is EnemyAnimation.Icon -> id to null
    }
}.toMap()
