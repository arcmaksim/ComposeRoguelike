package ru.meatgames.tomb.screen.compose.mainmenu

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.meatgames.tomb.domain.GameController
import javax.inject.Inject

@HiltViewModel
class MainMenuScreenViewModel @Inject constructor(
    private val gameController: GameController,
) : ViewModel() {
    
    private val _events = Channel<Event?>()
    val events: Flow<Event?> = _events.receiveAsFlow()
    
    fun newGame() {
        gameController.generateNewMap()
        _events.trySend(Event.NewGame)
    }
    
    fun exitGame() {
        _events.trySend(Event.Exit)
    }
    
    enum class Event {
        NewGame,
        Exit,
        ;
    }

}
