package ru.meatgames.tomb.screen.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.player.CharacterController
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    characterController: CharacterController,
) : ViewModel() {
    
    private val _events = Channel<InventoryEvent?>()
    val events: Flow<InventoryEvent?> = _events.receiveAsFlow()
    
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
        _events.trySend(InventoryEvent.Back)
    }

}
