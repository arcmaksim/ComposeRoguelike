package ru.meatgames.tomb.screen.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.screen.game.animation.ANIMATION_DURATION_MILLIS
import ru.meatgames.tomb.screen.game.component.GameScreenLoading
import ru.meatgames.tomb.screen.game.component.GameScreenMapContainer

@Composable
internal fun GameScreen(
    onWin: () -> Unit,
    onInventory: () -> Unit,
    onCharacterSheet: () -> Unit,
    viewModel: GameScreenViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
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
    val isIdle by viewModel.isWaitingForInput.collectAsStateWithLifecycle()
    
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
        is MapScreenState.Loading -> GameScreenLoading()
        is MapScreenState.Ready -> GameScreenMapContainer(
            mapState = mapState,
            isIdle = isIdle,
            playerHealth = mapState.playerHealth,
            animationDurationMillis = ANIMATION_DURATION_MILLIS,
            navigator = navigator,
            interactionController = interactionController,
        )
    }
}

