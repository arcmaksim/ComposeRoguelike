package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.enemy.EnemiesHolder
import ru.meatgames.tomb.domain.enemy.EnemiesMastermind
import ru.meatgames.tomb.domain.map.MapCreator
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.player.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.turn.CharactersTurnScheduler
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.domain.turn.finishesPlayerTurn
import ru.meatgames.tomb.domain.turn.hasAnimation
import ru.meatgames.tomb.logErrorWithTag
import ru.meatgames.tomb.logMessage
import java.util.Queue
import java.util.concurrent.LinkedTransferQueue
import javax.inject.Inject
import javax.inject.Singleton

interface GameController {
    
    val lastMapType: MapCreator.MapType

    val state: Flow<GameState>
    val dialogState: Flow<DialogState?>

    suspend fun startNewGame(
        mapType: MapCreator.MapType,
    )

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

    suspend fun showDialog(
        dialogState: DialogState,
    )

}

@Singleton
class GameControllerImpl @Inject constructor(
    private val mapCreator: MapCreator,
    private val characterController: CharacterController,
    private val enemiesHolder: EnemiesHolder,
    private val enemiesMastermind: EnemiesMastermind,
    private val charactersTurnScheduler: CharactersTurnScheduler,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
) : GameController {

    private val _state = MutableStateFlow<GameState>(GameState.Loading)
    override val state: Flow<GameState> = _state

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    override val dialogState: Flow<DialogState?> = _dialogState

    private var _lastMapType: MapCreator.MapType = MapCreator.MapType.MAIN
    override val lastMapType: MapCreator.MapType
        get() = _lastMapType

    private var currentTurnQueue: Queue<CharactersTurnScheduler.InitiativePosition> = LinkedTransferQueue()

    override suspend fun startNewGame(
        mapType: MapCreator.MapType,
    ) {
        characterController.modifyHealth(100)
        generateNewMap(mapType)
    }

    override suspend fun generateNewMap(
        mapType: MapCreator.MapType,
    ) {
        _dialogState.value = null
        _state.tryEmit(GameState.Loading)
        _lastMapType = mapType
        val configuration = mapCreator.createNewMap(mapType)
        characterController.setPosition(
            coordinates = configuration.startCoordinates,
        )
        calcTurnQueue(true)
        skipToPlayerTurn()
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

    override suspend fun blockPlayerTurn() {
        if (_state.value !is GameState.WaitingForInput) {
            "Trying to block player input when state is ${_state.value}".logErrorWithTag("GameState")
            return
        }

        _state.emit(GameState.ProcessingInput)
    }

    override suspend fun finishPlayerTurn(
        result: PlayerTurnResult?,
    ) {
        if (_state.value !is GameState.ProcessingInput) {
            "Trying to finish player turn when state is ${_state.value}".logErrorWithTag("GameState")
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
        if (_state.value !is GameState.AnimatingCharacter) {
            "Trying to finish player animation when state is ${_state.value}".logErrorWithTag("GameState")
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

    private suspend fun skipToPlayerTurn() {
        if (currentTurnQueue.isEmpty()) calcTurnQueue(false)

        while (currentTurnQueue.isNotEmpty()) {
            val element = currentTurnQueue.poll()

            if (element !is CharactersTurnScheduler.InitiativePosition.Enemy) {
                logMessage("TURN", "Player turn! ${characterController.characterState.getComponent<PositionComponent>()}")
                break
            }

            continue
        }

        _state.emit(GameState.WaitingForInput)
    }

    private suspend fun runEnemiesTurns() {
        _state.emit(GameState.ProcessingEnemies)

        if (currentTurnQueue.isEmpty()) calcTurnQueue(false)

        val results = mutableListOf<EnemyTurnResult>()

        while (currentTurnQueue.isNotEmpty()) {
            val element = currentTurnQueue.poll()

            if (element !is CharactersTurnScheduler.InitiativePosition.Enemy) {
                logMessage("TURN", "Player turn! ${characterController.characterState.getComponent<PositionComponent>()}")
                break
            }

            val enemy = enemiesHolder.getEnemy(element.enemyId) ?: continue
            logMessage("TURN", "${enemy.type} at ${enemy.getComponent<PositionComponent>()}")
            enemiesMastermind.takeTurn(enemy).let(results::add)
        }

        calcTurnQueue(false)
        results.resolveDialogState().updateState()
        _state.emit(GameState.AnimatingEnemies(results))
    }

    override suspend fun finishEnemiesAnimations() {
        if (_state.value !is GameState.AnimatingEnemies) {
            "Trying to finish enemies animations when state is ${_state.value}".logErrorWithTag("GameState")
            return
        }

        _state.emit(GameState.WaitingForInput)
    }

    override suspend fun finishCurrentAnimations() {
        when (val state = _state.value) {
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
                if (_dialogState.value?.isInterruptable == true) {
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

    override suspend fun showDialog(
        dialogState: DialogState,
    ) {
        _dialogState.value = dialogState
    }

}
