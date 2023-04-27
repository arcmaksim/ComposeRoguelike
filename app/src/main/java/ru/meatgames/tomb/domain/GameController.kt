package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.domain.component.asDirections
import ru.meatgames.tomb.domain.component.calculateVectorTo
import ru.meatgames.tomb.domain.component.isNextTo
import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.enemy.EnemiesController
import ru.meatgames.tomb.domain.enemy.EnemiesHolder
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.map.MapController
import ru.meatgames.tomb.domain.map.MapCreator
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.player.CharacterState
import ru.meatgames.tomb.domain.player.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.turn.CharactersTurnScheduler
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.domain.turn.finishesPlayerTurn
import ru.meatgames.tomb.domain.turn.hasAnimation
import ru.meatgames.tomb.logErrorWithTag
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.resolvedOffset
import java.util.Queue
import java.util.concurrent.LinkedTransferQueue
import javax.inject.Inject
import javax.inject.Singleton

interface GameController {
    
    val lastMapType: MapCreator.MapType
    
    val state: SharedFlow<GameState>
    val dialogState: StateFlow<DialogState?>
    
    suspend fun generateNewMap(
        mapType: MapCreator.MapType,
    )
    
    suspend fun blockPlayerTurn()
    
    suspend fun finishPlayerTurn(
        result: PlayerTurnResult?,
    )
    
    suspend fun startEnemiesTurn()
    
    suspend fun finishPlayerAnimation(
        result: PlayerTurnResult?,
    )
    
    suspend fun finishEnemiesAnimations()
    
    suspend fun finishCurrentAnimations()
    
    suspend fun closeCurrentDialog()
    
}

@Singleton
class GameControllerImpl @Inject constructor(
    private val mapCreator: MapCreator,
    private val mapController: MapController,
    private val tilesController: TilesController,
    private val characterController: CharacterController,
    private val enemiesHolder: EnemiesHolder,
    private val enemiesController: EnemiesController,
    private val charactersTurnScheduler: CharactersTurnScheduler,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
) : GameController {

    private val _state = MutableStateFlow<GameState>(GameState.Loading)
    override val state: StateFlow<GameState> = _state
    
    private val _dialogState = MutableStateFlow<DialogState?>(null)
    override val dialogState: StateFlow<DialogState?> = _dialogState
    
    private var _lastMapType: MapCreator.MapType = MapCreator.MapType.MAIN
    override val lastMapType: MapCreator.MapType
        get() = _lastMapType
    
    private var currentTurnQueue: Queue<CharactersTurnScheduler.InitiativePosition> = LinkedTransferQueue()
    
    override suspend fun generateNewMap(
        mapType: MapCreator.MapType,
    ) {
        _state.tryEmit(GameState.Loading)
        _lastMapType = mapType
        val configuration = mapCreator.createNewMap(mapType)
        characterController.setPosition(
            coordinates = configuration.startCoordinates,
        )
        calcTurnQueue(true)
        runEnemiesTurns()
    }
    
    private fun calcTurnQueue(
        clearQueue: Boolean,
    ) {
        val list = charactersTurnScheduler.produceSchedule(
            characterController.characterStateFlow.value,
            enemiesHolder.getEnemies(),
        )
        
        when {
            clearQueue || currentTurnQueue.isEmpty() -> currentTurnQueue = LinkedTransferQueue(list + list)
            else -> currentTurnQueue.addAll(list)
        }
    }
    
    private fun Enemy.takeTurn(
        player: CharacterState,
    ): EnemyTurnResult {
        val vectorToPlayer = position.calculateVectorTo(player.position)
        
        if (vectorToPlayer.isNextTo()) {
            val damage = 1
            attackPlayer(damage)
            
            return EnemyTurnResult.Attack(
                position = position.toCoordinates(),
                enemyId = id,
                direction = vectorToPlayer.asDirections().first(),
                amount = damage,
            )
        }
        
        val directionsToPlayer = vectorToPlayer.asDirections()
        
        directionsToPlayer.forEach { direction ->
            val newPosition = (position + direction.resolvedOffset).toCoordinates()
            mapController.getTile(newPosition)?.tile?.let { tile ->
                val tileInteraction = tile.objectEntityTile
                    ?.let(tilesController::hasObjectEntityNoInteraction)
                    ?: true
                if (tileInteraction && enemiesController.moveEnemy(id, direction)) {
                    return EnemyTurnResult.Move(
                        position = position.toCoordinates(),
                        enemyId = id,
                        direction = direction,
                    )
                }
            }
        }
        
        return EnemyTurnResult.SkipTurn(
            enemyId = id,
            position = position.toCoordinates(),
        )
    }
    
    private fun Enemy.attackPlayer(
        damage: Int,
    ) = characterController.modifyHealth(-damage)
    
    override suspend fun blockPlayerTurn() {
        if (state.value !is GameState.WaitingForInput) {
            "Trying to block player input when state is ${state.value}".logErrorWithTag("GameState")
            return
        }
        
        _state.emit(GameState.ProcessingInput)
    }
    
    override suspend fun finishPlayerTurn(
        result: PlayerTurnResult?,
    ) {
        if (state.value !is GameState.ProcessingInput) {
            "Trying to finish player turn when state is ${state.value}".logErrorWithTag("GameState")
            return
        }
        
        result?.let { turnResult ->
            mapInteractionResolver.resolvePlayerMove(turnResult)
            val nextState = when {
                turnResult is PlayerTurnResult.ContainerInteraction -> null
                turnResult.hasAnimation() -> GameState.AnimatingCharacter(turnResult)
                else -> GameState.PrepareForEnemies
            }
            turnResult.resolveDialogState().updateState()
            nextState?.let { _state.emit(it) }
        } ?: let {
            _state.emit(GameState.WaitingForInput)
        }
    }
    
    override suspend fun finishPlayerAnimation(
        result: PlayerTurnResult?,
    ) {
        if (state.value !is GameState.AnimatingCharacter) {
            "Trying to finish player animation when state is ${state.value}".logErrorWithTag("GameState")
            return
        }
        
        _state.emit(
            if (result?.finishesPlayerTurn() == true) {
                GameState.PrepareForEnemies
            } else {
                GameState.WaitingForInput
            },
        )
    }
    
    override suspend fun startEnemiesTurn() = runEnemiesTurns()
    
    private suspend fun runEnemiesTurns() {
        _state.emit(GameState.ProcessingEnemies)
        
        if (currentTurnQueue.size == 0) calcTurnQueue(false)
        
        val results = mutableListOf<EnemyTurnResult>()
        
        while (currentTurnQueue.size != 0) {
            val element = currentTurnQueue.poll()
            
            if (element !is CharactersTurnScheduler.InitiativePosition.Enemy) {
                logMessage("TURN", "Player turn! ${characterController.characterStateFlow.value.position}")
                break
            }
            
            val enemy = enemiesHolder.getEnemy(element.enemyId) ?: continue
            logMessage("TURN", "${enemy.type} at ${enemy.position}")
            enemy.takeTurn(characterController.characterStateFlow.value).let(results::add)
        }
        
        calcTurnQueue(false)
        results.resolveDialogState().updateState()
        _state.emit(GameState.AnimatingEnemies(results))
    }
    
    override suspend fun finishEnemiesAnimations() {
        if (state.value !is GameState.AnimatingEnemies) {
            "Trying to finish enemies animations when state is ${state.value}".logErrorWithTag("GameState")
            return
        }
        
        _state.emit(GameState.WaitingForInput)
    }
    
    override suspend fun finishCurrentAnimations() {
        when (val state = state.value) {
            is GameState.AnimatingCharacter -> finishPlayerAnimation(state.turnResult)
            is GameState.AnimatingEnemies -> finishEnemiesAnimations()
            else -> Unit
        }
    }
    
    override suspend fun closeCurrentDialog() {
        _dialogState.value = null
    }
    
    private fun DialogUpdateResult.updateState() {
        when (this) {
            is DialogUpdateResult.NewInteraction -> {
                _dialogState.value = dialogState
            }
            
            is DialogUpdateResult.DisruptInteraction -> {
                if (dialogState.value?.isInterruptable == true) {
                    _dialogState.value = null
                }
            }
            
            else -> Unit
        }
    }
    
    private fun PlayerTurnResult.resolveDialogState(): DialogUpdateResult = when {
        this is PlayerTurnResult.ContainerInteraction ->
            DialogUpdateResult.NewInteraction(DialogState.Container(itemContainerId))
        
        this is PlayerTurnResult.PickupItem && !isLastItem -> {
            DialogUpdateResult.NewInteraction(DialogState.Container(itemContainerId))
        }
        
        else -> DialogUpdateResult.DisruptInteraction
    }
    
    private fun List<EnemyTurnResult>.resolveDialogState(): DialogUpdateResult = when {
        any { it is EnemyTurnResult.Attack } -> DialogUpdateResult.DisruptInteraction
        else -> DialogUpdateResult.NoChange
    }
    
}
