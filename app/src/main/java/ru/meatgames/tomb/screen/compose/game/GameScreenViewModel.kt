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
import ru.meatgames.tomb.domain.PlayerMoveResult
import javax.inject.Inject

private const val TARGET_POINTS = 10

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    controller: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val gameController: GameController,
) : ViewModel() {
    
    private val _events = Channel<Any?>()
    val events: Flow<Any?> = _events.receiveAsFlow()

    private val _mapState = MutableStateFlow(controller.state.value)
    val mapState: StateFlow<MapScreenController.MapScreenState> = controller.state

    private val _animationState = Channel<PlayerAnimationState>()
    val animationState: Flow<PlayerAnimationState> = _animationState.receiveAsFlow()

    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle
    
    init {
        viewModelScope.launch {
            controller.state.collect { state ->
                _mapState.value = state
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

        _animationState.trySend(
            when (val result = mapInteractionController.makeMove(direction)) {
                is PlayerMoveResult.Block -> PlayerAnimationState.Shake()
                is PlayerMoveResult.Move -> PlayerAnimationState.Scroll(result.direction)
                else -> PlayerAnimationState.NoAnimation
            }
        )

        _isIdle.value = true
    }
    
    fun newMap() {
        gameController.generateNewMap()
    }

}
