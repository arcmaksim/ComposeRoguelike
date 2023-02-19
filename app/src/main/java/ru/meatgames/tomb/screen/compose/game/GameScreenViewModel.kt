package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.screen.compose.game.animation.GameScreenAnimationState
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import ru.meatgames.tomb.domain.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.PlayerMoveResult
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val controller: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
    private val gameController: GameController,
) : ViewModel() {
    
    private val _events = Channel<GameScreenEvent?>()
    val events: Flow<GameScreenEvent?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        GameScreenState(
            mapState = controller.state.value,
            playerAnimation = null,
        )
    )
    val state: Flow<GameScreenState> = _state
    
    private var pendingAnimation: GameScreenAnimationState? = null
        get() {
            val value = field
            field = null
            return value
        }
    private var previousMoveDirection: Direction? = null
        get() {
            val value = field
            field = null
            return value
        }
    
    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle
    
    init {
        viewModelScope.launch {
            controller.state.collect { state ->
                _state.value = GameScreenState(
                    mapState = state,
                    playerAnimation = pendingAnimation,
                    previousMoveDirection = previousMoveDirection,
                )
            }
        }
    }
    
    fun onMoveCharacter(
        direction: Direction,
    ) {
        if (!isIdle.value) return
        
        _isIdle.value = false
        
        val playerTurnResult = mapInteractionController.makeMove(direction) ?: let {
            _isIdle.value = true
            return
        }
        
        val animation = playerTurnResult.resolveGameScreenAnimationState()
        val interactionState = playerTurnResult.resolvePlayerInteractionState()
        
        if (animation.requiresManualUpdate() || interactionState != null) {
            updateStateNow(
                gameScreenAnimationState = animation,
                playerInteractionState = interactionState,
            )
        } else {
            animation.cacheState(playerTurnResult)
        }
    }
    
    private fun PlayerMoveResult.resolveGameScreenAnimationState(): GameScreenAnimationState? =
        when (this) {
            is PlayerMoveResult.Block -> GameScreenAnimationState.Shake()
            is PlayerMoveResult.Move -> GameScreenAnimationState.Scroll(direction)
            else -> null
        }
    
    private fun PlayerMoveResult.resolvePlayerInteractionState(): GameScreenInteractionState? =
        when (this) {
            is PlayerMoveResult.ContainerInteraction -> {
                GameScreenInteractionState.SearchingContainer(itemBag)
            }
            else -> null
        }
    
    private fun GameScreenAnimationState?.requiresManualUpdate(): Boolean = when (this) {
        is GameScreenAnimationState.Shake -> true
        else -> false
    }
    
    private fun updateStateNow(
        gameScreenAnimationState: GameScreenAnimationState?,
        playerInteractionState: GameScreenInteractionState?,
    ) {
        _state.update {
            it.copy(
                previousMoveDirection = null,
                playerAnimation = gameScreenAnimationState,
                interactionState = playerInteractionState,
            )
        }
        _isIdle.value = true
    }
    
    private fun GameScreenAnimationState?.cacheState(
        playerMoveResult: PlayerMoveResult,
    ) {
        pendingAnimation = this
        previousMoveDirection = (this as? GameScreenAnimationState.Scroll)?.direction
        
        mapInteractionResolver.resolvePlayerMove(playerMoveResult)
        
        _isIdle.value = true
    }
    
    fun newMap() {
        gameController.generateNewMap()
    }
    
    fun openInventory() {
        _events.trySend(GameScreenEvent.Inventory)
    }
    
}
