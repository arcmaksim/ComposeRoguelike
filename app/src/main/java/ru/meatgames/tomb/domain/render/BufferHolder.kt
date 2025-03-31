package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.map.MapTile
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import ru.meatgames.tomb.render.MapRenderTile
import javax.inject.Inject
import javax.inject.Singleton

const val BUFFER_SIZE_MODIFIER: Int = 1
private const val BUFFER_AMOUNT: Int = 2
private const val DEFAULT_OFFSET = Int.MAX_VALUE

class BufferHolder(
    viewportWidth: Int,
    viewportHeight: Int,
) {

    val width: Int = viewportWidth + BUFFER_SIZE_MODIFIER * 2
    val height: Int = viewportHeight + BUFFER_SIZE_MODIFIER * 2

    val horizontalCenter = width / 2
    val verticalCenter = height / 2

    private val size: Int = width * height

    var horizontalOffset: Int = DEFAULT_OFFSET
        private set
    var verticalOffset: Int = DEFAULT_OFFSET
        private set
    val offset: Coordinates
        get() = horizontalOffset to verticalOffset

    var horizontalMotion: Int = 0
        private set
    var verticalMotion: Int = 0
        private set

    private var bufferCounter: Int = 0
    private val bufferIndex: Int
        get() = bufferCounter % BUFFER_AMOUNT

    val mapBuffer: Array<MapTile?> = Array(size) { null }
    val fovBuffer: BooleanArray = BooleanArray(size) { false }
    // TODO: move to a separate entity to use in the gameplay
    val visibilityCache: BooleanArray = BooleanArray(size) { false }
    val floorRenderingBuffer: Array<FloorRenderTile?> = Array(size) { null }
    val objectRenderingBuffer: Array<ObjectRenderTile?> = Array(size) { null }
    private val _resultRenderingBuffer: Array<Array<MapRenderTile>> =
        Array(BUFFER_AMOUNT) { Array(size) { MapRenderTile.Empty } }

    val resultRenderingBuffer: Array<MapRenderTile>
        get() = _resultRenderingBuffer[bufferIndex]

    fun refresh(
        horizontalOffset: Int,
        verticalOffset: Int,
    ) {
        if (this.horizontalOffset != DEFAULT_OFFSET && this.verticalOffset != DEFAULT_OFFSET) {
            horizontalMotion = horizontalOffset - this.horizontalOffset
            verticalMotion = verticalOffset - this.verticalOffset
        }

        this.horizontalOffset = horizontalOffset
        this.verticalOffset = verticalOffset

        bufferCounter++

        mapBuffer.fill(null)
        fovBuffer.fill(true)
        visibilityCache.fill(true)
        floorRenderingBuffer.fill(null)
        objectRenderingBuffer.fill(null)
        resultRenderingBuffer.fill(MapRenderTile.Empty)
    }

    fun replaceWithPreviousResultRenderingBuffer(
        index: Int,
    ) {
        val adjustedIndex = index + horizontalMotion + verticalMotion * width
        _resultRenderingBuffer[(bufferIndex + 1) % BUFFER_AMOUNT]
            .getOrNull(adjustedIndex)
            ?.let {
                resultRenderingBuffer[index] = it
            }
    }

    fun areCoordinatesVisibleInFov(
        coordinates: Coordinates,
    ): Boolean {
        val bufferIndex = coordinates.first + coordinates.second * width
        return fovBuffer.getOrElse(bufferIndex) { false }
    }

    fun areCoordinatesVisibleInCache(
        coordinates: Coordinates,
    ): Boolean {
        val bufferIndex = coordinates.first + coordinates.second * width
        return visibilityCache.getOrElse(bufferIndex) { false }
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
            ?.takeIf { viewportWidth + BUFFER_SIZE_MODIFIER * 2 == it.width && viewportHeight + BUFFER_SIZE_MODIFIER * 2 == it.height }
            ?: let { BufferHolder(viewportWidth, viewportHeight).also { _cachedBufferHolder = it } }
    }

}