package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.domain.map.MapTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import javax.inject.Inject
import javax.inject.Singleton

const val BUFFER_SIZE_MODIFIER: Int = 1

class BufferHolder(
    viewportWidth: Int,
    viewportHeight: Int,
) {

    val width: Int = viewportWidth + BUFFER_SIZE_MODIFIER * 2
    val height: Int = viewportHeight + BUFFER_SIZE_MODIFIER * 2

    val horizontalCenter = width / 2
    val verticalCenter = height / 2

    private val size: Int = width * height

    var horizontalOffset: Int = 0
    var verticalOffset: Int = 0

    val mapBuffer: Array<MapTile?> = Array(size) { null }
    val visibilityBuffer: BooleanArray = BooleanArray(size) { false }
    val floorRenderingBuffer: Array<FloorRenderTile?> = Array(size) { null }
    val objectRenderingBuffer: Array<ObjectRenderTile?> = Array(size) { null }
    val resultRenderingBuffer: Array<MapRenderTile> = Array(size) { MapRenderTile.Empty }

    fun clear() {
        mapBuffer.fill(null)
        visibilityBuffer.fill(true)
        floorRenderingBuffer.fill(null)
        objectRenderingBuffer.fill(null)
        resultRenderingBuffer.fill(MapRenderTile.Empty)
    }

}

@Singleton
class BufferHolderFactory @Inject constructor() {

    private var _cachedBufferHolder: BufferHolder? = null
    val cachedBufferHolder: BufferHolder
        get() {
            val bufferHolder = _cachedBufferHolder
            require(bufferHolder != null) { "BufferHolder needs to be initialized first" }
            return bufferHolder
        }

    fun get(
        viewportWidth: Int,
        viewportHeight: Int,
    ): BufferHolder {
        return _cachedBufferHolder
            ?.takeIf { viewportWidth + BUFFER_SIZE_MODIFIER == it.width && viewportHeight + BUFFER_SIZE_MODIFIER == it.height }
            ?: let { BufferHolder(viewportWidth, viewportHeight).also { _cachedBufferHolder = it } }
    }

}