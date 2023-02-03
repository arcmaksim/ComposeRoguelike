package ru.meatgames.tomb.screen.compose.game

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.*
import ru.meatgames.tomb.domain.MapScreenController
import ru.meatgames.tomb.domain.PlayerMapInteractionController
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    controller: MapScreenController,
    private val mapInteractionController: PlayerMapInteractionController,
) : ViewModel() {

    val mapState: StateFlow<MapScreenController.MapScreenState> = controller.state

    private val _isIdle = MutableStateFlow(true)
    val isIdle: StateFlow<Boolean> = _isIdle

    fun onMoveCharacter(
        moveDirection: Direction,
    ) {
        if (!isIdle.value) return

        _isIdle.value = false

        mapInteractionController.makeMove(moveDirection)

        _isIdle.value = true
    }

}
