package ru.meatgames.tomb.screen.game.component

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.awaitAll
import ru.meatgames.tomb.R
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.design.backgroundColor
import ru.meatgames.tomb.design.component.IconButton
import ru.meatgames.tomb.design.component.IllustrationButton
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.map.EnemiesAnimations
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.presentation.camera.animation.CameraAnimationState
import ru.meatgames.tomb.presentation.camera.animation.assembleAnimations
import ru.meatgames.tomb.presentation.enemies.EnemyAnimation
import ru.meatgames.tomb.presentation.multiply
import ru.meatgames.tomb.screen.game.GameScreenInteractionController
import ru.meatgames.tomb.screen.game.GameScreenNavigator
import ru.meatgames.tomb.screen.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.game.LocalTileSize
import ru.meatgames.tomb.screen.game.animation.ANIMATION_DURATION_MILLIS
import ru.meatgames.tomb.screen.game.animation.EnemyAnimationState
import ru.meatgames.tomb.screen.game.interactionControllerPreviewStub
import ru.meatgames.tomb.screen.game.navigatorPreviewStub

@Preview
@Composable
private fun GameScreenMapContainerPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    
    GameScreenMapContainer(
        mapState = gameScreenMapContainerPreviewMapReadyState(themeAssets),
        isIdle = true,
        playerHealth = HealthComponent(10),
        animationDurationMillis = ANIMATION_DURATION_MILLIS,
        navigator = navigatorPreviewStub,
        interactionController = interactionControllerPreviewStub,
    )
}

private fun CameraAnimationState?.produceInitialOffset(
    savedCameraAnimationId: Int,
    currentCameraAnimationId: Int,
    tileSize: IntSize,
): IntOffset = when {
    FeatureToggles.getToggleValue(FeatureToggle.SkipCameraAnimations) -> IntOffset.Zero
    this == null -> IntOffset.Zero
    savedCameraAnimationId == currentCameraAnimationId -> IntOffset.Zero
    this is CameraAnimationState.Smooth -> offset.multiply(tileSize).multiply(-1)
    else -> IntOffset.Zero
}

@Composable
internal fun GameScreenMapContainer(
    mapState: MapScreenState.Ready,
    isIdle: Boolean,
    playerHealth: HealthComponent,
    animationDurationMillis: Int,
    navigator: GameScreenNavigator,
    interactionController: GameScreenInteractionController,
) = BoxWithConstraints(
    modifier = Modifier
        .background(backgroundColor)
        .fillMaxSize(),
) {
    val screenWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
    val tileDimension = screenWidth / mapState.viewportWidth
    val tileSize = IntSize(tileDimension, tileDimension)
    
    val cameraAnimationId = mapState.cameraAnimation?.hashCode() ?: -1
    var playedCameraAnimation by rememberSaveable { mutableIntStateOf(-1) }
    
    val view = LocalView.current
    val shakeOffset = remember(mapState.cameraAnimation) {
        mutableStateOf(IntOffset.Zero)
    }
    val enemiesAnimationUpdates = emptyMap<EnemyId, EnemyAnimationState?>()
    /*val enemiesAnimationUpdates = remember(enemiesAnimations) {
        mutableStateOf(enemiesAnimations?.toMap(tileDimension) ?: emptyMap())
    }*/
    val horizontalOffset = IntOffset(
        x = (screenWidth - (tileDimension * mapState.viewportWidth)) / 2 + shakeOffset.value.x,
        y = 0,
    )
    
    // Movement offsets
    val initialMovementOffset = remember(mapState.cameraAnimation) {
        mapState.cameraAnimation.produceInitialOffset(
            savedCameraAnimationId = playedCameraAnimation,
            currentCameraAnimationId = cameraAnimationId,
            tileSize = tileSize,
        )
    }
    val animatedMovementOffset = remember(mapState.cameraAnimation) {
        mutableStateOf(IntOffset.Zero)
    }
    
    LaunchedEffect(mapState.cameraAnimation) {
        if (mapState.cameraAnimation == null) {
            playedCameraAnimation = -1
            return@LaunchedEffect
        }
        
        val currentCameraAnimation = mapState.cameraAnimation.hashCode()
        if (currentCameraAnimation == playedCameraAnimation) return@LaunchedEffect
        
        playedCameraAnimation = currentCameraAnimation
        awaitAll(
            *mapState.cameraAnimation.assembleAnimations(
                animationDurationMillis = animationDurationMillis,
                tileSize = tileSize,
                shakeOffset = shakeOffset,
                animatedOffset = animatedMovementOffset,
                view = view,
            ),
        )
    }
    
    /*LaunchedEffect(playerAnimation) {
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
    }*/
    
    /*LaunchedEffect(enemiesAnimations) {
        when {
            enemiesAnimations == null -> return@LaunchedEffect
            
            else -> {
                awaitAll(
                    *enemiesAnimations.assembleEnemiesAnimations2(
                        animationDurationMillis = animationDurationMillis,
                        view = view,
                        onAttack = interactionController::onEnemyAnimationEvent,
                        tileDimension = tileDimension,
                    ) { it, state ->
                        enemiesAnimationUpdates.value = enemiesAnimationUpdates.value.toMutableMap().apply {
                            this[it] = state
                        }
                    },
                )
            }
        }
    }*/
    
    val baseModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
        .align(Alignment.Center)
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(tileDimension, tileDimension),
        LocalHorizontalOffset provides horizontalOffset,
        LocalBackgroundColor provides backgroundColor,
    ) {
        val modifier = baseModifier.offset { shakeOffset.value }
        
        GameScreenMap(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            animationDurationMillis = animationDurationMillis,
            animatedOffset = animatedMovementOffset.value,
            initialOffset = initialMovementOffset,
            tilesAnimation = mapState.tilesAnimation,
        )
        
        GameScreenCharacter(
            modifier = modifier,
            viewportWidth = mapState.viewportWidth,
            viewportHeight = mapState.viewportHeight,
            characterRenderData = mapState.characterRenderData,
        )
        
        /*GameScreenEnemies(
            modifier = modifier,
            tiles = mapState.tiles,
            tilesWidth = mapState.tilesWidth,
            tilesPadding = mapState.tilesPadding,
            tilesToReveal = mapState.tilesToFadeIn,
            tilesToFade = mapState.tilesToFadeOut,
            animationStates = enemiesAnimationUpdates.value,
            animatedOffset = animatedMovementOffset.value,
            initialOffset = initialMovementOffset,
            revealedTilesAlpha = revealedTilesAlpha.floatValue,
            fadedTilesAlpha = fadedTilesAlpha.floatValue,
            characterFrameIndex = characterAnimationFrameIndex,
        )*/
    }
    
    GameScreenControls(
        modifier = baseModifier,
        interactionController = interactionController,
    )
    
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