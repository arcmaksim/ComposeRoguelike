package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerAnimationState
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import ru.meatgames.tomb.domain.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.PlayerMoveResult
import javax.inject.Inject

private const val TARGET_POINTS = 10

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val controller: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
    private val gameController: GameController,
) : ViewModel() {
    
    private val _events = Channel<Any?>()
    val events: Flow<Any?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        GameScreenState(
            mapState = controller.state.value,
            playerAnimation = PlayerAnimationState.NoAnimation(),
        )
    )
    val state: Flow<GameScreenState> = _state
    
    private var pendingAnimation: PlayerAnimationState = PlayerAnimationState.NoAnimation()
        get() {
            val value = field
            field = PlayerAnimationState.NoAnimation()
            return value
        }
    private var previousModeDirection: Direction? = null
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
                    previousMoveDirection = previousModeDirection,
                )
                
                (state as? MapScreenController.MapScreenState.Ready)
                    ?.takeIf { it.points == TARGET_POINTS }
                    ?.let { _events.send(Any()) }
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
        
        val animation = when (val result = mapInteractionController.makeMove(direction)) {
            is PlayerMoveResult.Block -> PlayerAnimationState.Shake()
            is PlayerMoveResult.Move -> PlayerAnimationState.Scroll(result.direction)
            else -> PlayerAnimationState.NoAnimation()
        }
        
        pendingAnimation = animation
        previousModeDirection = (animation as? PlayerAnimationState.Scroll)?.direction
        
        mapInteractionResolver.resolvePlayerMove(playerTurnResult)
        
        _isIdle.value = true
    }
    
    fun newMap() {
        gameController.generateNewMap()
    }
    
}

data class GameScreenState(
    val mapState: MapScreenController.MapScreenState = MapScreenController.MapScreenState.Loading,
    val playerAnimation: PlayerAnimationState = PlayerAnimationState.NoAnimation(),
    val previousMoveDirection: Direction? = null,
)
