package ru.meatgames.tomb.screen.compose.game.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.GameController
import javax.inject.Inject

@HiltViewModel
class GameScreenDialogVM @Inject constructor(
    private val gameController: GameController,
): ViewModel() {
    
    private val _events = Channel<GameScreenDialogEvent?>()
    val events: Flow<GameScreenDialogEvent?> = _events.receiveAsFlow()
    
    fun generateNewMap() {
        viewModelScope.launch {
            dismissDialog()
            gameController.generateNewMap(gameController.lastMapType)
        }
    }
    
    fun showFeatureToggles() {
        viewModelScope.launch {
            dismissDialog()
            _events.send(GameScreenDialogEvent.NavigateToFeatureToggles)
        }
    }
    
    fun closeDialog() {
        viewModelScope.launch {
            dismissDialog()
        }
    }
    
    private suspend fun dismissDialog() {
        gameController.closeCurrentDialog()
        _events.send(GameScreenDialogEvent.CloseDialog)
    }
    
}
