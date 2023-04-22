package ru.meatgames.tomb.domain

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val mapController: MapController,
    private val tilesController: TilesController,
    private val characterController: CharacterController,
    private val enemiesHolder: EnemiesHolder,
    private val enemiesController: EnemiesController,
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
            mapController.getTile(newPosition)?.tile?.let {  tile ->
                val tileInteraction = tile.objectEntityTile?.let(tilesController::hasObjectEntityNoInteraction) ?: true
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
            
            val enemy = enemiesHolder.getEnemy(element.enemyId) ?: continue
            logMessage("TURN", "${enemy.type} at ${enemy.position}")
            enemy.takeTurn(characterController.characterStateFlow.value)?.let(results::add)
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
