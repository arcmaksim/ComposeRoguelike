package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.new_models.tile.domain.ThemedTile
import ru.meatgames.tomb.new_models.tile.domain.ThemedTilePurposeDefinition
import ru.meatgames.tomb.new_models.tile.domain.toThemedTile
import ru.meatgames.tomb.new_models.tile.domain.GeneralTilePurpose
import ru.meatgames.tomb.resolvedOffsets
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
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
        tile: ThemedGameMapTile,
    ) {
        val resolvedTileReplacementOnUse = tile.`object`?.resolveTileReplacementOnUse() ?: return

        mapTerraformer.changeObject(
            x = mapX,
            y = mapY,
            objectTile = resolvedTileReplacementOnUse,
        )
    }

    private fun ThemedTile.resolveTileReplacementOnUse(): ThemedTile? {
        val definition = purposeDefinition as? ThemedTilePurposeDefinition.General ?: return null
        return when (definition.purpose) {
            GeneralTilePurpose.ClosedDoor -> ru.meatgames.tomb.new_models.tile.domain.ThemedTilePurposeDefinition.General(
                purpose = GeneralTilePurpose.OpenDoor,
            ).toThemedTile(theme)

            else -> null
        }
    }

}
