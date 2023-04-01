package ru.meatgames.tomb.screen.compose.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.MapCreator
import javax.inject.Inject

@HiltViewModel
class MainMenuScreenViewModel @Inject constructor(
    private val gameController: GameController,
) : ViewModel() {
    
    private val _events = Channel<Event?>()
    val events: Flow<Event?> = _events.receiveAsFlow()
    
    fun launchNewGame() {
        viewModelScope.launch {
            gameController.generateNewMap(MapCreator.MapType.MAIN)
        }
        _events.trySend(Event.NewGame)
    }
    
    fun lunchPlayground() {
        viewModelScope.launch {
            gameController.generateNewMap(MapCreator.MapType.PLAYGROUND)
        }
        _events.trySend(Event.NewGame)
    }
    
    fun exitGame() {
        _events.trySend(Event.Exit)
    }
    
    enum class Event {
        NewGame,
        Exit,
    }

}
