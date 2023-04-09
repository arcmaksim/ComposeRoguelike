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
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.GameState
import ru.meatgames.tomb.domain.ItemsHolder
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.screen.compose.game.animation.EnemiesAnimationState
import ru.meatgames.tomb.screen.compose.game.animation.PlayerAnimationState
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val mapScreenController: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val gameController: GameController,
    private val itemsHolder: ItemsHolder,
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
                    enemiesAnimations = it.toEnemiesAnimationsState(),
                    interactionState = it.toPlayerInteractionState(),
                )
            }.launchIn(this)
        }
    }
    
    private fun MapScreenController.MapScreenState.toPlayerAnimation(): PlayerAnimationState? {
        return (if (this is MapScreenController.MapScreenState.Ready &&
            turnResultsToAnimate is MapScreenController.MapScreenCharacterTurnResults.Player) {
            turnResultsToAnimate.turnResult
        } else {
            null
        })?.resolvePlayerAnimationState()
    }
    
    private fun MapScreenController.MapScreenState.toPlayerInteractionState(): PlayerInteractionState? {
        return (if (this is MapScreenController.MapScreenState.Ready &&
            turnResultsToAnimate is MapScreenController.MapScreenCharacterTurnResults.Player) {
            turnResultsToAnimate.turnResult
        } else {
            null
        })?.resolvePlayerInteractionState()
    }
    
    private fun MapScreenController.MapScreenState.toEnemiesAnimationsState(): List<Pair<EnemyId, EnemiesAnimationState>>? {
        return if (this is MapScreenController.MapScreenState.Ready &&
            turnResultsToAnimate is MapScreenController.MapScreenCharacterTurnResults.Enemies) {
            turnResultsToAnimate.turnResults.resolveEnemyAnimationState()
        } else {
            null
        }
    }
    
    private var latestTurnResult: PlayerTurnResult? = null
    
    override fun processCharacterMoveInput(
        direction: Direction,
    ) {
        if (!isIdle.value) return
        
        viewModelScope.launch {
            gameController.blockPlayerTurn()
            latestTurnResult = mapInteractionController.resolveMoveResult(direction)
            gameController.finishPlayerTurn(latestTurnResult)
        }
    }
    
    private fun PlayerTurnResult.resolvePlayerAnimationState(): PlayerAnimationState? =
        when (this) {
            is PlayerTurnResult.Block -> PlayerAnimationState.Shake
            is PlayerTurnResult.Move -> PlayerAnimationState.Scroll(direction)
            is PlayerTurnResult.Attack -> PlayerAnimationState.Attack(direction)
            else -> null
        }
    
    private fun PlayerTurnResult.resolvePlayerInteractionState(): PlayerInteractionState? =
        when (this) {
            is PlayerTurnResult.ContainerInteraction -> {
                PlayerInteractionState.SearchingContainer(
                    coordinates = coordinates,
                    itemContainerId = itemContainerId,
                    items = itemsHolder.getItems(itemIds),
                )
            }
            
            else -> null
        }
    
    private fun List<EnemyTurnResult>.resolveEnemyAnimationState(): List<Pair<EnemyId, EnemiesAnimationState>> =
        map { result ->
            when (result) {
                is EnemyTurnResult.Move -> {
                    result.enemyId to EnemiesAnimationState.Move(
                        direction = result.direction,
                    )
                }
            
                is EnemyTurnResult.Attack -> {
                    result.enemyId to EnemiesAnimationState.Attack(
                        direction = result.direction,
                    )
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
            gameController.blockPlayerTurn()
            latestTurnResult = mapInteractionController.pickItem(itemContainerId, itemId)
            gameController.finishPlayerTurn(latestTurnResult)
        }
    }
    
    override suspend fun finishPlayerAnimation() {
        gameController.finishPlayerAnimation(latestTurnResult)
    }
    
    override suspend fun finishEnemiesAnimation() {
        gameController.finishEnemiesAnimations()
    }
    
}
