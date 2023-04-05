package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerMapInteractionResolver @Inject constructor(
    private val characterController: CharacterController,
    private val mapTerraformer: MapTerraformer,
    private val itemsController: ItemsController,
    private val itemsHolder: ItemsHolder,
) {

    fun resolvePlayerMove(
        result: PlayerTurnResult,
    ): ResolveResult {
        return when (result) {
            is PlayerTurnResult.Interaction -> {
                useTile(
                    mapX = result.coordinates.first,
                    mapY = result.coordinates.second,
                    objectEntityTile = result.tile,
                )
                ResolveResult.None
            }
        
            is PlayerTurnResult.Move -> {
                characterController.move(result.direction)
                ResolveResult.None
            }
        
            is PlayerTurnResult.PickupItem -> {
                val item = itemsController.takeItem(
                    coordinates = result.coordinates,
                    itemContainerId = result.itemContainerId,
                    itemId = result.itemId,
                ) ?: return ResolveResult.None
            
                characterController.addItem(item)
            
                itemsHolder.getMapContainerId(result.coordinates)
                    ?.let { ResolveResult.None }
                    ?: ResolveResult.Clear
            }
        
            else -> ResolveResult.None
        }
    }
    
    enum class ResolveResult {
        None,
        Clear,
    }

    private fun useTile(
        mapX: Int,
        mapY: Int,
        objectEntityTile: ObjectEntityTile,
    ) {
        val resolvedTileReplacementOnUse = objectEntityTile.resolveTileReplacementOnUse()

        mapTerraformer.changeObject(
            x = mapX,
            y = mapY,
            objectEntityTile = resolvedTileReplacementOnUse,
        )
    }

    private fun ObjectEntityTile.resolveTileReplacementOnUse(): ObjectEntityTile? = when (this) {
        ObjectEntityTile.DoorClosed -> ObjectEntityTile.DoorOpened
        else -> null
    }

}
