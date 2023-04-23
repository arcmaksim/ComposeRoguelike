package ru.meatgames.tomb.screen.compose.game.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.GameController
import javax.inject.Inject

@HiltViewModel
class GameScreenDialogVM @Inject constructor(
    private val gameController: GameController,
): ViewModel() {
    
    fun generateNewMap() {
        viewModelScope.launch {
            gameController.generateNewMap(gameController.lastMapType)
        }
    }
    
}