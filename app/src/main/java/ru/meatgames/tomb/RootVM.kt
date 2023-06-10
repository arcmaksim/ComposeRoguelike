package ru.meatgames.tomb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.GameController
import javax.inject.Inject

@HiltViewModel
class RootVM @Inject constructor(
    private val gameController: GameController,
) : ViewModel() {
    
    val dialogState = gameController.dialogState
    
    fun finishCurrentAnimations() {
        viewModelScope.launch {
            gameController.finishCurrentAnimations()
        }
    }
    
    fun closeDialog() {
        viewModelScope.launch {
            gameController.closeCurrentDialog()
        }
    }
    
}
