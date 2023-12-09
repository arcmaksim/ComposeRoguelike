package ru.meatgames.tomb.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.domain.DialogState
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.GameState
import ru.meatgames.tomb.domain.PlayerInputProcessor
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.map.cameraAnimation
import ru.meatgames.tomb.domain.map.ready
import ru.meatgames.tomb.presentation.MapScreenController
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.presentation.camera.animation.resolveCameraUpdateState
import ru.meatgames.tomb.screen.game.animation.EnemyAnimationEvent
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    mapScreenController: MapScreenController,
    private val gameController: GameController,
    private val playerInputProcessor: PlayerInputProcessor,
    private val characterController: CharacterController,
) : ViewModel(), GameScreenNavigator, GameScreenInteractionController {
    
    private var queuedInput: Direction? = null
    
    private val _events = Channel<GameScreenEvent?>()
    val events: Flow<GameScreenEvent?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        GameScreenState(
            mapState = mapScreenController.state.value,
        )
    )
    val state: StateFlow<GameScreenState> = _state
    
    private val _isWaitingForInput = MutableStateFlow(true)
    val isWaitingForInput: StateFlow<Boolean> = _isWaitingForInput
    
    init {
        viewModelScope.launch {
            mapScreenController.state
                .collect { mapScreenState ->
                    _state.update {
                        GameScreenState.mapState.modify(it) { mapScreenState }
                    }
                }
        }
        
        gameController.state
            .onEach {
                _isWaitingForInput.value = it == GameState.WaitingForInput
            }
            .launchIn(viewModelScope)
        
        isWaitingForInput
            .filter { it }
            .mapNotNull { queuedInput?.also { queuedInput = null } }
            .onEach(::processCharacterMoveInput)
            .launchIn(viewModelScope)
    }
    
    override fun processCharacterMoveInput(
        direction: Direction,
    ) {
        if (!isWaitingForInput.value) {
            if (FeatureToggles.getToggleValue(FeatureToggle.InputQueue)) {
                queuedInput = direction
            }
            return
        }
        
        viewModelScope.launch {
            playerInputProcessor
                .processPlayerInput(direction)
                ?.setUnhandledCameraUpdate()
        }
    }
    
    private fun PlayerTurnResult.setUnhandledCameraUpdate() {
        resolveCameraUpdateState()?.let { cameraAnimationState ->
            _state.update { screenState ->
                GameScreenState.mapState
                    .ready
                    .cameraAnimation
                    .modify(screenState) { cameraAnimationState }
            }
        }
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
        /*viewModelScope.launch {
            gameController.finishPlayerTurn(null)
        }*/
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
    
    override fun skipTurn() {
        if (!isWaitingForInput.value) return
        viewModelScope.launch {
            gameController.blockPlayerTurn()
            gameController.finishTurn()
        }
    }
    
    override fun onEnemyAnimationEvent(
        event: EnemyAnimationEvent,
    ) {
        characterController.modifyHealth(-1)
    }
}
