package ru.meatgames.tomb.screen.compose.death

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.meatgames.tomb.Scene
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.asNavigationToCommand
import javax.inject.Inject

@HiltViewModel
class DeathScreenVM @Inject constructor(
    private val scenesNavigator: ScenesNavigator,
): ViewModel() {
    
    fun navigateToMainMenu() {
        scenesNavigator.navigateTo(Scene.MainMenu.asNavigationToCommand(true))
    }
    
}
