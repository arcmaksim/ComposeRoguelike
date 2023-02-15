package ru.meatgames.tomb.domain

import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerMapInteractionResolver @Inject constructor(
    private val characterController: CharacterController,
    private val mapTerraformer: MapTerraformer,
) {

    fun resolvePlayerMove(
        result: PlayerMoveResult,
    ) {
        when (result) {
            is PlayerMoveResult.Interaction -> {
                useTile(
                    mapX = result.coordinates.first,
                    mapY = result.coordinates.second,
                    objectEntityTile = result.tile,
                )
            }
        
            is PlayerMoveResult.Move -> {
                characterController.move(result.direction)
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
        ObjectEntityTile.Gismo -> {
            characterController.increasePoint()
            null
        }
        else -> null
    }

}
