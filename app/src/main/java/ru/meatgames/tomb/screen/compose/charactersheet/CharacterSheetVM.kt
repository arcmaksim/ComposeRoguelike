package ru.meatgames.tomb.screen.compose.charactersheet

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
class CharacterSheetVM @Inject constructor(
    characterController: CharacterController,
    private val scenesNavigator: ScenesNavigator,
): ViewModel() {
    
    private val _state = MutableStateFlow(
        characterController.playerStateSnapshot.run {
            CharacterSheetState(
                stats = getComponent(),
                health = getComponent(),
                offensiveBehaviorCard = offenseBehaviorCard,
                defensiveBehaviorCard = defenceBehaviorCard,
                supportBehaviorCard = supportBehaviorCard,
            )
        }
    )
    val state: StateFlow<CharacterSheetState> = _state
    
    init {
        viewModelScope.launch {
            characterController.playerStateFlow.collect {
                _state.value = CharacterSheetState(
                    stats = it.getComponent(),
                    health = it.getComponent(),
                    offensiveBehaviorCard = it.offenseBehaviorCard,
                    defensiveBehaviorCard = it.defenceBehaviorCard,
                    supportBehaviorCard = it.supportBehaviorCard,
                )
            }
        }
    }
    
    fun onBack() {
        scenesNavigator.navigateTo(ScenesNavigator.Command.NavigateBack)
    }
    
}