package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.model.tile.domain.FloorEntityTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import javax.inject.Inject

class MapDecoratorPipeline @Inject constructor(
    private val mapDecorators: Set<@JvmSuppressWildcards MapRenderTilesDecorator>,
    private val bufferHolderFactory: BufferHolderFactory,
) {

    // Assumes tiles is a square
    fun produceRenderTilesFrom() {
        bufferHolderFactory.cachedBufferHolder.fillRenderingBuffers()

        mapDecorators.forEach { decorator ->
            decorator.apply()
        }
    }

    private fun BufferHolder.fillRenderingBuffers() {
        var index = -1
        mapBuffer.iterator().forEach {
            index++
            floorRenderingBuffer[index] = it?.floorEntityTile?.toFloorRenderTile()
            objectRenderingBuffer[index] = it?.objectEntityTile?.toObjectRenderTile()
        }
    }

    private fun FloorEntityTile.toFloorRenderTile(): FloorRenderTile = when (this) {
        FloorEntityTile.Floor -> FloorRenderTile.Floor
    }

    private fun ObjectEntityTile.toObjectRenderTile(): ObjectRenderTile = when (this) {
        ObjectEntityTile.DoorClosed -> ObjectRenderTile.DoorClosed
        ObjectEntityTile.DoorOpened -> ObjectRenderTile.DoorOpened
        ObjectEntityTile.StairsDown -> ObjectRenderTile.StairsDown
        ObjectEntityTile.StairsUp -> ObjectRenderTile.StairsUp
        ObjectEntityTile.Wall -> ObjectRenderTile.Wall0
    }

}
