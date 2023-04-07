package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerMapInteractionResolver @Inject constructor(
    private val characterController: CharacterController,
    private val mapTerraformer: MapTerraformer,
    private val itemsController: ItemsController,
    private val enemiesHolder: EnemiesHolder,
) {

    fun resolvePlayerMove(
        result: PlayerTurnResult,
    ) {
        when (result) {
            is PlayerTurnResult.Interaction -> {
                useTile(
                    mapX = result.coordinates.first,
                    mapY = result.coordinates.second,
                    objectEntityTile = result.tile,
                )
            }
        
            is PlayerTurnResult.Move -> {
                characterController.move(result.direction)
            }
        
            is PlayerTurnResult.PickupItem -> {
                val (item, isContainerEmpty) = itemsController.takeItem(
                    itemContainerId = result.itemContainerId,
                    itemId = result.itemId,
                ) ?: return
            
                characterController.addItem(item)
            }
            
            is PlayerTurnResult.Attack -> {
                val coordinates = (characterController.characterStateFlow.value.position + result.direction.resolvedOffset).toCoordinates()
                enemiesHolder.tryToInflictDamage(coordinates, 2)
            }
        
            else -> Unit
        }
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
