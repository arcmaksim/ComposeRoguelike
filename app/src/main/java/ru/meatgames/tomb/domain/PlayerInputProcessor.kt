package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.player.PlayerMapInteractionController
import ru.meatgames.tomb.domain.player.PlayerMapInteractionResolver
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.domain.turn.finishesPlayerTurn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerInputProcessor @Inject constructor(
    private val gameController: GameController,
    private val mapInteractionController: PlayerMapInteractionController,
    private val mapInteractionResolver: PlayerMapInteractionResolver,
) {
    
    suspend fun processPlayerInput(
        direction: Direction,
    ) {
        gameController.blockPlayerTurn()
        val result = mapInteractionController.resolveMoveResult(direction)
        mapInteractionResolver.resolvePlayerMove(result)
        
        if (result.finishesPlayerTurn()) {
            gameController.finishTurn()
            return
        }
        
        // run enemies
        gameController.finishTurn()
    }
    
    suspend fun skipTurn() {
        gameController.blockPlayerTurn()
        mapInteractionResolver.resolvePlayerMove(PlayerTurnResult.SkipTurn)
        // run enemies
        gameController.finishTurn()
    }
    
    suspend fun processPlayerInput(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) {
        gameController.blockPlayerTurn()
        val result = mapInteractionController.pickItem(itemContainerId, itemId)
        mapInteractionResolver.resolvePlayerMove(result)
        
        if (result.finishesPlayerTurn()) {
            gameController.finishTurn()
            return
        }
        
        // run enemies
        gameController.finishTurn()
    }
    
}
