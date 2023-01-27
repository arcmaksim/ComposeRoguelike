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
    private val characterController: CharacterController,
    private val mapTerraformer: MapTerraformer,
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
        ) ?: return null

        return when {
            tile.objectEntityTile?.let(tilesController::hasObjectEntityInteraction) == true -> {
                useTile(
                    mapX = mapX,
                    mapY = mapY,
                    objectEntityTile = tile.objectEntityTile,
                )
                PlayerMoveResult.Interaction
            }

            tile.objectEntityTile?.let(tilesController::hasObjectEntityNoInteraction) == true
                    || tile.objectEntityTile == null -> {
                characterController.move(direction)
                PlayerMoveResult.Move(direction)
            }

            else -> PlayerMoveResult.Block
        }
    }

    // will be used by AI?
    fun useTile(
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
