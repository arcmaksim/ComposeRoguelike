package ru.meatgames.tomb.screen.compose.featuretoggle

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import javax.inject.Inject

@HiltViewModel
class FeatureToggleScreenVM @Inject constructor(
    private val scenesNavigator: ScenesNavigator,
): ViewModel() {
    
    val state = FeatureToggles.state
    
    fun updateToggle(
        key: FeatureToggle,
        value: Boolean,
    ) = FeatureToggles.updateToggle(key, value)
    
    fun navigateBack() {
        scenesNavigator.navigateTo(ScenesNavigator.Command.NavigateBack)
    }
    
}
