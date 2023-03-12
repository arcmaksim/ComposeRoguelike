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
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.ItemsHolder
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.screen.compose.game.animation.GameScreenAnimationState
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import ru.meatgames.tomb.domain.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.PlayerTurnResult
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val controller: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
    private val gameController: GameController,
    private val itemsHolder: ItemsHolder,
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
    private var clearInteractionState: Unit? = null
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
                    interactionState = _state.value.interactionState?.takeIf { clearInteractionState == null },
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
    
        playerTurnResult.process()
    }
    
    private fun PlayerTurnResult.process() {
        val animation = resolveGameScreenAnimationState()
        val interactionState = resolvePlayerInteractionState()
    
        if (animation.requiresManualUpdate() || interactionState != null) {
            updateStateNow(
                gameScreenAnimationState = animation,
                playerInteractionState = interactionState,
            )
        } else {
            animation.cacheState(this)
        }
    }
    
    private fun PlayerTurnResult.resolveGameScreenAnimationState(): GameScreenAnimationState? =
        when (this) {
            is PlayerTurnResult.Block -> GameScreenAnimationState.Shake()
            is PlayerTurnResult.Move -> GameScreenAnimationState.Scroll(direction)
            else -> null
        }
    
    private fun PlayerTurnResult.resolvePlayerInteractionState(): GameScreenInteractionState? =
        when (this) {
            is PlayerTurnResult.ContainerInteraction -> {
                clearInteractionState = Unit
                GameScreenInteractionState.SearchingContainer(
                    coordinates = coordinates,
                    itemContainerId = itemContainerId,
                    items = itemsHolder.getItems(itemIds),
                )
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
        playerTurnResult: PlayerTurnResult,
    ) {
        pendingAnimation = this
        previousMoveDirection = (this as? GameScreenAnimationState.Scroll)?.direction
        
        val resolveResult = mapInteractionResolver.resolvePlayerMove(playerTurnResult)
        
        if (playerTurnResult is PlayerTurnResult.PickupItem &&
            resolveResult == PlayerMapInteractionResolver.ResolveResult.Clear) {
            clearInteractionState = Unit
        }
        
        _isIdle.value = true
    }
    
    fun newMap() {
        gameController.generateNewMap()
    }
    
    fun openInventory() {
        _events.trySend(GameScreenEvent.Inventory)
    }
    
    fun closeInteractionMenu() {
        _state.update {
            it.copy(
                interactionState = null,
            )
        }
    }
    
    fun pickUpItem(
        coordinates: Coordinates,
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) {
        mapInteractionController.pickItem(
            coordinates = coordinates,
            itemContainerId = itemContainerId,
            itemId = itemId,
        ).process()
    }
    
}
