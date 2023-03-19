package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.logMessage

class LevelMap(
    val width: Int,
    val height: Int,
) {
    
    private val array = Array(width * height) { MapTile.initialTile }
    
    private val _state = MutableStateFlow(array.toList())
    val state: StateFlow<List<MapTile>> = _state
    
    private val editor = EditorImpl()
    
    fun getTile(
        x: Int,
        y: Int,
    ): MapTileWrapper? {
        val capturedState = state.value
        val index = calcIndex(x, y)
        if (index < 0 || index >= capturedState.size) {
            logMessage(
                tag = "LevelMap",
                message = "getTile - index out of bounds - [0 .. $index ${capturedState.size}]",
            )
            return null
        }
        return MapTileWrapper(
            tile = capturedState[calcIndex(x, y)],
            x = x,
            y = y,
        )
    }
    
    fun updateSingleTile(
        x: Int,
        y: Int,
        update: MapTile.() -> MapTile,
    ) {
        val index = calcIndex(x, y)
        if (!updateTile(index, update)) return
        _state.value = array.toList()
    }
    
    private fun updateTile(
        index: Int,
        update: MapTile.() -> MapTile,
    ): Boolean {
        val mapSize = state.value.size
        if (index < 0 || index >= mapSize) {
            logMessage(
                tag = "LevelMap",
                message = "getTile - index out of bounds - [0 .. $index $mapSize]",
            )
            return false
        }
        
        array[index] = array[index].update()
        return true
    }
    
    fun updateBatch(
        updateFunc: (Editor.() -> Unit),
    ) {
        updateFunc.invoke(editor)
        _state.value = array.toList()
    }
    
    private fun calcIndex(
        x: Int,
        y: Int,
    ) = x + y * width
    
    
    interface Editor {
        
        fun updateSingleTile(
            x: Int,
            y: Int,
            update: MapTile.() -> MapTile,
        )
        
    }
    
    inner class EditorImpl : Editor {
        
        override fun updateSingleTile(
            x: Int,
            y: Int,
            update: MapTile.() -> MapTile,
        ) {
            if (!updateTile(calcIndex(x, y), update)) return
        }
        
    }
    
}
