package ru.meatgames.tomb.domain

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.meatgames.tomb.domain.component.resolveDirectionTo
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.domain.turn.finishesPlayerTurn
import ru.meatgames.tomb.domain.turn.hasAnimation
import ru.meatgames.tomb.logMessage
import java.util.Queue
import java.util.concurrent.LinkedTransferQueue
import javax.inject.Inject
import javax.inject.Singleton

interface GameController {
    
    val lastMapType: MapCreator.MapType
    
    val state: SharedFlow<GameState>
    
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
    
}

@Singleton
class GameControllerImpl @Inject constructor(
    private val mapCreator: MapCreator,
    private val characterController: CharacterController,
    private val enemiesHolder: EnemiesHolder,
    private val charactersTurnScheduler: CharactersTurnScheduler,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
) : GameController {
    
    private val _state = MutableSharedFlow<GameState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    ).apply {
        tryEmit(GameState.Loading)
    }
    override val state: SharedFlow<GameState> = _state
    
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
        calcTurnQueue()
        runEnemiesTurns()
    }
    
    private fun calcTurnQueue() {
        val list = charactersTurnScheduler.produceSchedule(
            characterController.characterStateFlow.value,
            enemiesHolder.getEnemies(),
        )
        
        when {
            currentTurnQueue.isEmpty() -> currentTurnQueue = LinkedTransferQueue(list + list)
            else -> currentTurnQueue.addAll(list)
        }
    }
    
    private fun CharactersTurnScheduler.InitiativePosition.Enemy.takeTurn(
        player: CharacterState,
    ): EnemyTurnResult? {
        val directionToThePlayer = enemy.position.resolveDirectionTo(player.position) ?: return null
        
        val damage = 1
        enemy.attackPlayer(damage)
        return EnemyTurnResult.Attack(
            direction = directionToThePlayer,
            amount = damage,
        )
    }
    
    private fun Enemy.attackPlayer(
        damage: Int,
    ) = characterController.modifyHealth(-damage)
    
    override suspend fun blockPlayerTurn() {
        _state.emit(GameState.ProcessingInput)
    }
    
    override suspend fun finishPlayerTurn(
        result: PlayerTurnResult?,
    ) {
        result?.let {
            mapInteractionResolver.resolvePlayerMove(it)
            val nextState = when {
                it.hasAnimation() -> GameState.AnimatingCharacter(it)
                else -> GameState.PrepareForEnemies
            }
            _state.emit(nextState)
        } ?: let {
            _state.emit(GameState.WaitingForInput)
        }
    }
    
    override suspend fun finishPlayerAnimation(
        result: PlayerTurnResult?,
    ) = _state.emit(
        if (result?.finishesPlayerTurn() == true) {
            GameState.PrepareForEnemies
        } else {
            GameState.WaitingForInput
        },
    )
    
    override suspend fun startEnemiesTurn() = runEnemiesTurns()
    
    private suspend fun runEnemiesTurns() {
        _state.emit(GameState.ProcessingEnemies)
        
        if (currentTurnQueue.size == 0) calcTurnQueue()
        
        val results = mutableListOf<EnemyTurnResult>()
        
        while (currentTurnQueue.size != 0) {
            val element = currentTurnQueue.poll()
            
            if (element !is CharactersTurnScheduler.InitiativePosition.Enemy) {
                logMessage("TURN", "Player turn! ${characterController.characterStateFlow.value.position}")
                break
            }
            
            logMessage("TURN", "${element.enemy.type} at ${element.enemy.position}")
            element.takeTurn(characterController.characterStateFlow.value)?.let {
                results.add(it)
            }
        }
        
        calcTurnQueue()
        _state.emit(GameState.AnimatingEnemies(results))
    }
    
    override suspend fun finishEnemiesAnimations() {
        _state.emit(GameState.WaitingForInput)
    }
    
}

sealed class GameState {
    
    object Loading : GameState()
    
    object WaitingForInput : GameState()
    
    object ProcessingInput : GameState()
    
    data class AnimatingCharacter(
        val turnResult: PlayerTurnResult,
    ) : GameState()
    
    // Needs for clearing animations
    object PrepareForEnemies : GameState()
    
    object ProcessingEnemies : GameState()
    
    data class AnimatingEnemies(
        // TODO ordered set
        val results: List<EnemyTurnResult>,
    ) : GameState()
    
}
