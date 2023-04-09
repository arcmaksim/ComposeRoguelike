package ru.meatgames.tomb.screen.compose.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.screen.compose.game.component.GameScreenLoading
import ru.meatgames.tomb.screen.compose.game.component.GameScreenMapContainer

private const val ANIMATION_TIME = 300

@Composable
internal fun GameScreen(
    viewModel: GameScreenViewModel,
    onWin: () -> Unit,
    onInventory: () -> Unit,
    onCharacterSheet: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                GameScreenEvent.NavigateToWinScreen -> onWin()
                GameScreenEvent.NavigateToInventory -> onInventory()
                GameScreenEvent.NavigateToCharacterSheet -> onCharacterSheet()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isIdle by viewModel.isIdle.collectAsStateWithLifecycle()
    
    GameScreenContent(
        state = state,
        isIdle = isIdle,
        navigator = viewModel,
        interactionController = viewModel,
    )
}

@Composable
private fun GameScreenContent(
    state: GameScreenState,
    isIdle: Boolean,
    navigator: GameScreenNavigator,
    interactionController: GameScreenInteractionController,
) {
    when (val mapState = state.mapState) {
        is MapScreenController.MapScreenState.Loading -> GameScreenLoading()
        is MapScreenController.MapScreenState.Ready -> GameScreenMapContainer(
            mapState = mapState,
            isIdle = isIdle,
            playerHealth = mapState.playerHealth,
            playerAnimation = state.playerAnimation,
            enemiesAnimations = state.enemiesAnimations,
            interactionState = state.interactionState,
            animationTime = ANIMATION_TIME,
            navigator = navigator,
            interactionController = interactionController,
        )
    }
}

