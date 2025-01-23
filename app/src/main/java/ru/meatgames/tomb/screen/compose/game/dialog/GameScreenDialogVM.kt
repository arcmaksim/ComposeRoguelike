package ru.meatgames.tomb.screen.compose.game.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.meatgames.tomb.Scene
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.asNavigationToCommand
import ru.meatgames.tomb.domain.GameController
import javax.inject.Inject

@HiltViewModel
class GameScreenDialogVM @Inject constructor(
    private val gameController: GameController,
    private val scenesNavigator: ScenesNavigator,
): ViewModel() {
    
    fun generateNewMap() {
        viewModelScope.launch {
            dismissDialog()
            gameController.generateNewMap(gameController.lastMapType)
        }
    }
    
    fun showFeatureToggles() {
        viewModelScope.launch {
            dismissDialog()
            scenesNavigator.navigateTo(Scene.FeatureToggles.asNavigationToCommand())
        }
    }
    
    fun closeDialog() {
        viewModelScope.launch {
            dismissDialog()
        }
    }
    
    private suspend fun dismissDialog() {
        gameController.closeCurrentDialog()
        scenesNavigator.navigateTo(ScenesNavigator.Command.NavigateBack)
    }
    
}
