package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.model.tile.domain.Tile
import ru.meatgames.tomb.model.tile.domain.TilePurposeDefinition
import ru.meatgames.tomb.model.tile.domain.toTile
import ru.meatgames.tomb.model.tile.domain.GeneralTilePurpose
import ru.meatgames.tomb.resolvedOffsets
import ru.meatgames.tomb.screen.compose.game.MapTile
import javax.inject.Inject

class PlayerMapInteractionController @Inject constructor(
    private val characterController: CharacterController,
    private val mapTerraformer: MapTerraformer,
    private val mapController: MapController,
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
    ) {
        val (offsetX, offsetY) = direction.resolvedOffsets
        val capturedFlow = characterStateFlow.value
        val mapX = capturedFlow.mapX + offsetX
        val mapY = capturedFlow.mapY + offsetY

        val tile = mapController.getTile(
            x = mapX,
            y = mapY,
        ) ?: return

        when {
            tile.`object`?.isUsable == true -> {
                useTile(
                    mapX = mapX,
                    mapY = mapY,
                    tile = tile,
                )
            }

            tile.`object` == null || tile.`object`.isPassable -> {
                characterController.move(direction)
            }
        }
    }

    fun useTile(
        mapX: Int,
        mapY: Int,
        tile: MapTile,
    ) {
        val resolvedTileReplacementOnUse = tile.`object`?.resolveTileReplacementOnUse() ?: return

        mapTerraformer.changeObject(
            x = mapX,
            y = mapY,
            objectTile = resolvedTileReplacementOnUse,
        )
    }

    private fun Tile.resolveTileReplacementOnUse(): Tile? {
        val definition = purposeDefinition as? TilePurposeDefinition.General ?: return null
        return when (definition.purpose) {
            GeneralTilePurpose.ClosedDoor -> ru.meatgames.tomb.model.tile.domain.TilePurposeDefinition.General(
                purpose = GeneralTilePurpose.OpenDoor,
            ).toTile(theme)

            else -> null
        }
    }

}
