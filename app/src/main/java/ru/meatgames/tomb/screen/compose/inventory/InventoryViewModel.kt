package ru.meatgames.tomb.screen.compose.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.domain.player.CharacterController
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    characterController: CharacterController,
    private val scenesNavigator: ScenesNavigator,
) : ViewModel() {
    
    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state
    
    init {
        viewModelScope.launch {
            characterController.characterStateFlow.collect {
                _state.value = InventoryState(it.inventory)
            }
        }
    }
    
    fun onBack() {
        scenesNavigator.navigateTo(ScenesNavigator.Command.NavigateBack)
    }

}
