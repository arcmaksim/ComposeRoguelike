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
import ru.meatgames.tomb.domain.PlayerAnimationState
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
    
    private var pendingAnimation: PlayerAnimationState? = null
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
        
        val animation = when (playerTurnResult) {
            is PlayerMoveResult.Block -> PlayerAnimationState.Shake()
            is PlayerMoveResult.Move -> PlayerAnimationState.Scroll(playerTurnResult.direction)
            else -> null
        }
        
        if (animation.isWithoutStateUpdate()) {
            animation.consumeAnimationWithoutStateUpdate()
        } else {
            animation.consumeAnimationWithStateUpdate(playerTurnResult)
        }
    }
    
    private fun PlayerAnimationState?.isWithoutStateUpdate(): Boolean = when (this) {
        is PlayerAnimationState.Shake -> true
        else -> false
    }
    
    private fun PlayerAnimationState?.consumeAnimationWithoutStateUpdate() {
        _state.update {
            it.copy(
                previousMoveDirection = null,
                playerAnimation = this,
            )
        }
        _isIdle.value = true
    }
    
    private fun PlayerAnimationState?.consumeAnimationWithStateUpdate(
        playerMoveResult: PlayerMoveResult,
    ) {
        pendingAnimation = this
        previousMoveDirection = (this as? PlayerAnimationState.Scroll)?.direction
    
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
