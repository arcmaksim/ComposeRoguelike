package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.resolvedOffsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerMapInteractionController @Inject constructor(
    characterController: CharacterController,
    private val mapController: MapController,
    private val tilesController: TilesController,
) {
    
    private val characterStateFlow = characterController.characterStateFlow
    
    init {
        // TODO update
        GlobalScope.launch {
            characterStateFlow.collect {}
        }
    }
    
    fun makeMove(
        direction: Direction,
    ): PlayerMoveResult? {
        val (offsetX, offsetY) = direction.resolvedOffsets
        val capturedFlow = characterStateFlow.value
        val mapX = capturedFlow.mapX + offsetX
        val mapY = capturedFlow.mapY + offsetY
        
        val tile = mapController.getTile(
            x = mapX,
            y = mapY,
        )?.tile ?: return null
        
        return when (val mapObject = tile.mapObject) {
            is MapTile.MapObject.Object -> mapObject.objectEntityTile.resolveMoveResult(
                direction = direction,
                characterState = capturedFlow,
            )
            is MapTile.MapObject.Item -> PlayerMoveResult.ItemBagInteraction(mapObject.item)
            else -> PlayerMoveResult.Move(direction)
        }
    }
    
    private fun ObjectEntityTile.resolveMoveResult(
        direction: Direction,
        characterState: CharacterState,
    ): PlayerMoveResult {
        val (offsetX, offsetY) = direction.resolvedOffsets
        val mapX = characterState.mapX + offsetX
        val mapY = characterState.mapY + offsetY
        
        return when {
            this.let(tilesController::hasObjectEntityInteraction) -> {
                PlayerMoveResult.Interaction(
                    coordinates = mapX to mapY,
                    tile = this,
                )
            }
            
            this.let(tilesController::hasObjectEntityNoInteraction) -> {
                PlayerMoveResult.Move(direction)
            }
            
            else -> PlayerMoveResult.Block
        }
    }
    
}
