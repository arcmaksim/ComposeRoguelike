package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.CharacterController
import javax.inject.Inject

@HiltViewModel
class CharacterSheetVM @Inject constructor(
    characterController: CharacterController,
): ViewModel() {
    
    private val _events = Channel<CharacterSheetEvent?>()
    val events: Flow<CharacterSheetEvent?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        characterController.characterStateFlow.value.run {
            CharacterSheetState(
                power = power,
                speed = speed,
                cunning = cunning,
                technique = technique,
            )
        }
    )
    val state: StateFlow<CharacterSheetState> = _state
    
    init {
        viewModelScope.launch {
            characterController.characterStateFlow.collect {
                _state.value = CharacterSheetState(
                    power = it.power,
                    speed = it.speed,
                    cunning = it.cunning,
                    technique = it.technique,
                )
            }
        }
    }
    
    fun onBack() {
        _events.trySend(CharacterSheetEvent.Back)
    }
    
}