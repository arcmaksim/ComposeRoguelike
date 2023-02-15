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

        return when {
            tile.objectEntityTile?.let(tilesController::hasObjectEntityInteraction) == true -> {
                PlayerMoveResult.Interaction(
                    coordinates = mapX to mapY,
                    tile = tile.objectEntityTile,
                )
            }

            tile.objectEntityTile?.let(tilesController::hasObjectEntityNoInteraction) == true
                    || tile.objectEntityTile == null -> {
                PlayerMoveResult.Move(direction)
            }

            else -> PlayerMoveResult.Block
        }
    }

}
