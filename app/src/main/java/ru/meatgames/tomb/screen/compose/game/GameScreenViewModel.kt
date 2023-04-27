package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.DialogState
import ru.meatgames.tomb.domain.map.EnemiesAnimations
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.GameState
import ru.meatgames.tomb.domain.PlayerInputProcessor
import ru.meatgames.tomb.domain.map.MapScreenController
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.map.MapScreenCharacterAnimations
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.domain.player.PlayerAnimation
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val mapScreenController: MapScreenController,
    private val gameController: GameController,
    private val playerInputProcessor: PlayerInputProcessor,
) : ViewModel(), GameScreenNavigator, GameScreenInteractionController {
    
    private val _events = Channel<GameScreenEvent?>()
    val events: Flow<GameScreenEvent?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        GameScreenState(
            mapState = mapScreenController.state.value,
            playerAnimation = null,
        )
    )
    val state: StateFlow<GameScreenState> = _state
    
    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle
    
    init {
        viewModelScope.launch {
            gameController.state.onEach { state ->
                _isIdle.value = state is GameState.WaitingForInput
                when (state) {
                    is GameState.PrepareForEnemies -> {
                        gameController.startEnemiesTurn()
                    }
                    else -> Unit
                }
            }.launchIn(this)
            mapScreenController.state.onEach {
                _state.value = GameScreenState(
                    mapState = it,
                    playerAnimation = it.toPlayerAnimation(),
                    enemiesAnimations = it.toEnemiesAnimations(),
                )
            }.launchIn(this)
        }
    }
    
    private fun MapScreenState.toPlayerAnimation(): PlayerAnimation? {
        return (if (this is MapScreenState.Ready &&
            turnResultsToAnimate is MapScreenCharacterAnimations.Player) {
            turnResultsToAnimate.turnResult
        } else {
            null
        })?.resolvePlayerAnimation()
    }
    
    private fun MapScreenState.toEnemiesAnimations(): EnemiesAnimations? {
        return if (this is MapScreenState.Ready &&
            turnResultsToAnimate is MapScreenCharacterAnimations.Enemies) {
            turnResultsToAnimate.animations
        } else {
            null
        }
    }
    
    override fun processCharacterMoveInput(
        direction: Direction,
    ) {
        if (!isIdle.value) return
        
        viewModelScope.launch {
            playerInputProcessor.processPlayerInput(direction)
        }
    }
    
    private fun PlayerTurnResult.resolvePlayerAnimation(): PlayerAnimation? =
        when (this) {
            is PlayerTurnResult.Block -> PlayerAnimation.Shake
            is PlayerTurnResult.Move -> PlayerAnimation.Move(direction)
            is PlayerTurnResult.Attack -> PlayerAnimation.Attack(direction)
            else -> null
        }
    
    override fun onNewMapRequest() {
        viewModelScope.launch {
            gameController.generateNewMap(gameController.lastMapType)
        }
    }
    
    override fun navigateToInventory() {
        _events.trySend(GameScreenEvent.NavigateToInventory)
    }
    
    override fun navigateToCharacterSheet() {
        _events.trySend(GameScreenEvent.NavigateToCharacterSheet)
    }
    
    override fun showDialog() {
        viewModelScope.launch {
            gameController.showDialog(DialogState.GameMenu)
        }
    }
    
    override fun closeInteractionMenu() {
        viewModelScope.launch {
            gameController.finishPlayerTurn(null)
        }
    }
    
    override fun itemSelected(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) {
        viewModelScope.launch {
            playerInputProcessor.processPlayerInput(
                itemContainerId = itemContainerId,
                itemId = itemId,
            )
        }
    }
    
    override fun finishPlayerAnimation() {
        viewModelScope.launch {
            gameController.finishPlayerAnimation(playerInputProcessor.latestTurnResult)
        }
    }
    
    override fun finishEnemiesAnimation() {
        viewModelScope.launch {
            gameController.finishEnemiesAnimations()
        }
    }
    
    override fun skipTurn() {
        if (!isIdle.value) return
        viewModelScope.launch {
            gameController.blockPlayerTurn()
            gameController.finishPlayerTurn(PlayerTurnResult.SkipTurn)
        }
    }
}


