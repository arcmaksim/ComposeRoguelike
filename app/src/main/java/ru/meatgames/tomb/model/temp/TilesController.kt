package ru.meatgames.tomb.model.temp

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TilesController @Inject constructor() {

    private val floorEntityTileProperties = mapOf(
        FloorEntityTile.Floor to TileEntityModifierProperty(TileEntityModifier.None),
    )

    private val objectEntityTileProperties = mapOf(
        ObjectEntityTile.Wall to TileEntityInteractionProperty(TileEntityInteraction.Block, false),
        ObjectEntityTile.DoorClosed to TileEntityInteractionProperty(TileEntityInteraction.Interaction, false),
        ObjectEntityTile.DoorOpened to TileEntityInteractionProperty(TileEntityInteraction.None, true),
        ObjectEntityTile.StairsUp to TileEntityInteractionProperty(TileEntityInteraction.Interaction, false),
        ObjectEntityTile.StairsDown to TileEntityInteractionProperty(TileEntityInteraction.None, true),
        ObjectEntityTile.Gismo to TileEntityInteractionProperty(TileEntityInteraction.Block, true),
    )

    fun getFloorEntityProperty(
        floorEntity: FloorEntityTile,
    ): TileEntityModifierProperty = floorEntityTileProperties[floorEntity]!!

    fun isFloorEntityHasNoModifier(
        floorEntity: FloorEntityTile,
    ): Boolean = floorEntityTileProperties[floorEntity]!!.modifier == TileEntityModifier.None

    fun getObjectEntityProperty(
        objectEntity: ObjectEntityTile,
    ): TileEntityInteractionProperty = objectEntityTileProperties[objectEntity]!!

    fun hasObjectEntityNoInteraction(
        objectEntity: ObjectEntityTile,
    ): Boolean = objectEntityTileProperties[objectEntity]!!.interaction == TileEntityInteraction.None

    fun hasObjectEntityInteraction(
        objectEntity: ObjectEntityTile,
    ): Boolean = objectEntityTileProperties[objectEntity]!!.interaction == TileEntityInteraction.Interaction

    // TODO: add tests to check mapping
    fun isObjectEntityVisibleThrough(
        objectEntity: ObjectEntityTile,
    ): Boolean = objectEntityTileProperties[objectEntity]!!.isVisibleThrough

}

enum class TileEntityInteraction {
    None,
    Block,
    Interaction,
}

enum class TileEntityModifier {
    None,
    BlockMovement,
}

class TileEntityModifierProperty(
    val modifier: TileEntityModifier,
)

class TileEntityInteractionProperty(
    val interaction: TileEntityInteraction,
    val isVisibleThrough: Boolean,
)

