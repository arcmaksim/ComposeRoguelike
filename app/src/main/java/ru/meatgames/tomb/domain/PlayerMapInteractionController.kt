package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerMapInteractionController @Inject constructor(
    characterController: CharacterController,
    private val mapController: MapController,
    private val tilesController: TilesController,
    private val itemsHolder: ItemsHolder,
    private val enemiesHolder: EnemiesHolder,
) {
    
    private val characterStateFlow = characterController.characterStateFlow
    
    init {
        // TODO update
        GlobalScope.launch {
            characterStateFlow.collect {}
        }
    }
    
    fun resolveMoveResult(
        direction: Direction,
    ): PlayerTurnResult? {
        val (offsetX, offsetY) = direction.resolvedOffset
        val capturedFlow = characterStateFlow.value
        val coordinates = (capturedFlow.position.x + offsetX) to (capturedFlow.position.y + offsetY)
        
        val tile = mapController.getTile(coordinates)?.tile ?: return null
        
        val itemContainer = itemsHolder.getItemContainer(coordinates)
        val enemy = enemiesHolder.getEnemy(coordinates)
        
        return when {
            itemContainer != null -> PlayerTurnResult.ContainerInteraction(
                coordinates = coordinates,
                itemContainerId = itemContainer.id,
                itemIds = itemContainer.itemIds,
            )
    
            enemy != null -> PlayerTurnResult.Attack(direction = direction)
    
            tile.objectEntityTile != null -> tile.objectEntityTile.resolveMoveResult(
                direction = direction,
                coordinates = coordinates,
            )
            
            else -> PlayerTurnResult.Move(direction)
        }
    }
    
    private fun ObjectEntityTile.resolveMoveResult(
        direction: Direction,
        coordinates: Coordinates,
    ): PlayerTurnResult = when {
        let(tilesController::hasObjectEntityInteraction) -> {
            PlayerTurnResult.Interaction(
                coordinates = coordinates,
                tile = this,
            )
        }
        
        let(tilesController::hasObjectEntityNoInteraction) -> {
            PlayerTurnResult.Move(direction)
        }
        
        else -> PlayerTurnResult.Block
    }
    
    fun pickItem(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ): PlayerTurnResult = PlayerTurnResult.PickupItem(
        itemContainerId = itemContainerId,
        itemId = itemId,
    )
    
}
