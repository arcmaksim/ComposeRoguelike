package ru.meatgames.tomb.screen.compose.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Scene
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.asNavigationToCommand
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.map.MapCreator
import javax.inject.Inject

@HiltViewModel
class MainMenuScreenViewModel @Inject constructor(
    private val gameController: GameController,
    private val scenesNavigator: ScenesNavigator,
) : ViewModel() {
    
    private val _events = Channel<Event?>()
    val events: Flow<Event?> = _events.receiveAsFlow()
    
    fun launchNewGame() = newGame(MapCreator.MapType.MAIN)

    fun lunchMechanicsPlayground() = newGame(MapCreator.MapType.MECHANICS_PLAYGROUND)

    fun lunchTestingPlayground() = newGame(MapCreator.MapType.TESTING_PLAYGROUND)

    private fun newGame(
        mapType: MapCreator.MapType,
    ) {
        viewModelScope.launch {
            gameController.startNewGame(mapType)
            scenesNavigator.navigateTo(Scene.MainGame.asNavigationToCommand())
        }
    }
    
    fun exitGame() {
        _events.trySend(Event.Exit)
    }
    
    enum class Event {
        Exit,
    }

}
