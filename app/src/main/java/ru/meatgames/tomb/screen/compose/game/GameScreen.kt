package ru.meatgames.tomb.screen.compose.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.domain.map.MapScreenController
import ru.meatgames.tomb.screen.compose.game.animation.ANIMATION_DURATION_MILLIS
import ru.meatgames.tomb.screen.compose.game.component.GameScreenLoading
import ru.meatgames.tomb.screen.compose.game.component.GameScreenMapContainer

@Composable
internal fun GameScreen(
    viewModel: GameScreenViewModel,
    onWin: () -> Unit,
    onInventory: () -> Unit,
    onCharacterSheet: () -> Unit,
    onDialog: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                GameScreenEvent.NavigateToWinScreen -> onWin()
                GameScreenEvent.NavigateToInventory -> onInventory()
                GameScreenEvent.NavigateToCharacterSheet -> onCharacterSheet()
                GameScreenEvent.ShowDialog -> onDialog()
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
            animationDurationMillis = ANIMATION_DURATION_MILLIS,
            navigator = navigator,
            interactionController = interactionController,
        )
    }
}

