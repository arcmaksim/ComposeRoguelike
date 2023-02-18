package ru.meatgames.tomb.screen.compose.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.screen.compose.game.component.GameScreenLoading
import ru.meatgames.tomb.screen.compose.game.component.GameScreenMapContainer

private const val ANIMATION_TIME = 300
private const val HERO_IDLE_ANIMATION_FRAMES = 2
private const val HERO_IDLE_ANIMATION_FRAME_TIME = 600

@Composable
internal fun GameScreen(
    viewModel: GameScreenViewModel,
    onWin: () -> Unit,
    onInventory: () -> Unit,
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
    
    val state by viewModel.state.collectAsState(GameScreenState())
    
    GameScreenContent(
        state = state,
        onCharacterMove = viewModel::onMoveCharacter,
        onMapGeneration = viewModel::newMap,
        onInventory = viewModel::openInventory,
    )
}

@Composable
private fun GameScreenContent(
    state: GameScreenState,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
    onInventory: () -> Unit,
) {
    when (val mapState = state.mapState) {
        is MapScreenController.MapScreenState.Loading -> GameScreenLoading()
        is MapScreenController.MapScreenState.Ready -> GameScreenMapContainer(
            mapState = mapState,
            playerAnimation = state.playerAnimation,
            previousMoveDirection = state.previousMoveDirection,
            animationTime = ANIMATION_TIME,
            heroIdleAnimationFrames = HERO_IDLE_ANIMATION_FRAMES,
            heroIdleAnimationFrameTime = HERO_IDLE_ANIMATION_FRAME_TIME,
            onCharacterMove = onCharacterMove,
            onMapGeneration = onMapGeneration,
            onInventory = onInventory,
        )
    }
}

