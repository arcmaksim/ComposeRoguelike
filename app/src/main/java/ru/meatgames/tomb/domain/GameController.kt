package ru.meatgames.tomb.domain

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    
    suspend fun finishPlayerTurn()
    
}

@Singleton
class GameControllerImpl @Inject constructor(
    private val mapCreator: MapCreator,
    private val characterController: CharacterController,
    private val enemiesHolder: EnemiesHolder,
    private val charactersTurnScheduler: CharactersTurnScheduler,
) : GameController {
    
    private val _state = MutableSharedFlow<GameState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
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
        finishPlayerTurn()
    }
    
    override suspend fun finishPlayerTurn() {
        _state.tryEmit(GameState.ProcessingTurns)
        
        if (currentTurnQueue.size == 0) calcTurnQueue()
        
        while (currentTurnQueue.size != 0) {
            val element = currentTurnQueue.poll()
            
            if (element !is CharactersTurnScheduler.InitiativePosition.Enemy) {
                logMessage("TURN", "Player turn!")
                break
            }
    
            logMessage("TURN", "${element.enemy.type} at ${element.enemy.position}")
        }
        
        calcTurnQueue()
        _state.tryEmit(GameState.WaitingForPlayer)
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
    
}

enum class GameState {
    Loading,
    WaitingForPlayer,
    ProcessingTurns,
}
