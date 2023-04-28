package ru.meatgames.tomb.screen.compose.featuretoggle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.config.FeatureToggleKey
import javax.inject.Inject

@HiltViewModel
class FeatureToggleScreenVM @Inject constructor(): ViewModel() {
    
    private val _events = Channel<FeatureToggleScreenEvent?>()
    val events: Flow<FeatureToggleScreenEvent?> = _events.receiveAsFlow()
    
    val state = FeatureToggles.state
    
    fun updateToggle(
        key: FeatureToggleKey,
        value: Boolean,
    ) = FeatureToggles.updateToggle(key, value)
    
    fun navigateBack() {
        viewModelScope.launch {
            _events.send(FeatureToggleScreenEvent.Back)
        }
    }
    
}
