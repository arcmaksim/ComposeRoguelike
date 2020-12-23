package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val viewportWidth = 7
private const val viewportHeight = 7

class GameScreenViewModel : ViewModel() {

    private val cachedGameMapTiles = Array(viewportWidth * viewportHeight) { GameMapTile() }

    private val _visibleMapChunk = MutableLiveData(
        GameMapChunk(viewportWidth, viewportHeight, cachedGameMapTiles.toList())
    )
    val visibleMapChunk: LiveData<GameMapChunk> = _visibleMapChunk

}

data class GameMapChunk(
    val width: Int,
    val height: Int,
    val gameMapTiles: List<GameMapTile>
)