package ru.meatgames.tomb.screen.compose.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
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
    
    val state by viewModel.state.collectAsState(GameScreenState())
    
    GameScreenContent(
        state = state,
        onCharacterMove = viewModel::onMoveCharacter,
        onMapGeneration = viewModel::newMap,
        onInventory = viewModel::openInventory,
        onCharacterSheet = viewModel::openCharacterSheet,
        onCloseInteractionMenu = viewModel::closeInteractionMenu,
        onItemSelected = viewModel::pickUpItem,
    )
}

@Composable
private fun GameScreenContent(
    state: GameScreenState,
    onCharacterMove: (Direction) -> Unit,
    onMapGeneration: () -> Unit,
    onInventory: () -> Unit,
    onCharacterSheet: () -> Unit,
    onCloseInteractionMenu: () -> Unit,
    onItemSelected: (Coordinates, ItemContainerId, ItemId) -> Unit,
) {
    when (val mapState = state.mapState) {
        is MapScreenController.MapScreenState.Loading -> GameScreenLoading()
        is MapScreenController.MapScreenState.Ready -> GameScreenMapContainer(
            mapState = mapState,
            playerAnimation = state.playerAnimation,
            interactionState = state.interactionState,
            previousMoveDirection = state.previousMoveDirection,
            animationTime = ANIMATION_TIME,
            onCharacterMove = onCharacterMove,
            onMapGeneration = onMapGeneration,
            onInventory = onInventory,
            onCharacterSheet = onCharacterSheet,
            onCloseInteractionMenu = onCloseInteractionMenu,
            onItemSelected = onItemSelected,
        )
    }
}

