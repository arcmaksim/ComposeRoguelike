package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.player.PlayerMapInteractionController
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerInputProcessor @Inject constructor(
    private val gameController: GameController,
    private val mapInteractionController: PlayerMapInteractionController,
) {
    
    var latestTurnResult: PlayerTurnResult? = null
        private set
    
    suspend fun processPlayerInput(
        direction: Direction,
    ) {
        gameController.blockPlayerTurn()
        latestTurnResult = mapInteractionController.resolveMoveResult(direction)
        gameController.finishPlayerTurn(latestTurnResult)
    }
    
    suspend fun processPlayerInput(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) {
        gameController.blockPlayerTurn()
        latestTurnResult = mapInteractionController.pickItem(itemContainerId, itemId)
        gameController.finishPlayerTurn(latestTurnResult)
    }
    
}
