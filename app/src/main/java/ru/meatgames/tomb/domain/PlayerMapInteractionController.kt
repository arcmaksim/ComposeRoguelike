package ru.meatgames.tomb.domain

import ru.meatgames.tomb.ThemedMapController
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTile
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTilePurposeDefinition
import ru.meatgames.tomb.new_models.themed.domain.tile.toThemedTile
import ru.meatgames.tomb.new_models.tile.GeneralTilePurpose
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
import javax.inject.Inject

class PlayerMapInteractionController @Inject constructor(
    private val mapController: ThemedMapController,
) {

    fun useTile(
        tile: ThemedGameMapTile,
        mapX: Int,
        mapY: Int,
    ) {
        val resolvedTileReplacementOnUse = tile.`object`?.resolveTileReplacementOnUse() ?: return

        mapController.changeObjectTile(
            x = mapX,
            y = mapY,
            tile = resolvedTileReplacementOnUse,
        )
    }

    private fun ThemedTile.resolveTileReplacementOnUse(): ThemedTile? {
        val definition = purposeDefinition as? ThemedTilePurposeDefinition.General ?: return null
        return when (definition.purpose) {
            GeneralTilePurpose.ClosedDoor -> ThemedTilePurposeDefinition.General(
                purpose = GeneralTilePurpose.OpenDoor,
            ).toThemedTile(theme)

            else -> null
        }
    }

}
