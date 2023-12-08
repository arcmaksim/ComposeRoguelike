package ru.meatgames.tomb.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FeatureToggles {
    
    private val _state = MutableStateFlow(
        listOf(
            FeatureToggleState(
                key = FeatureToggle.SkipCameraAnimations,
                title = "Skip camera animations",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggle.SkipPlayerAnimations,
                title = "Skip player animations",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggle.SkipEnemiesAnimations,
                title = "Skip enemies animations",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggle.ShowEnemiesHealthBar,
                title = "Show enemies health bar",
                value = true,
            ),
            FeatureToggleState(
                key = FeatureToggle.ShowMovementControls,
                title = "Show movement controls",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggle.InputQueue,
                title = "Enable input queue",
                value = true,
            ),
        )
    )
    val state: StateFlow<List<FeatureToggleState>> = _state
    
    val themeOverride: String? = null
    
    private val cachedToggleValues: MutableMap<FeatureToggle, Boolean> = _state.value
        .associate { it.key to it.value }
        .toMutableMap()
    
    fun getToggleValue(
        key: FeatureToggle,
    ): Boolean = cachedToggleValues[key]!!
    
    fun updateToggle(
        key: FeatureToggle,
        value: Boolean,
    ) {
        _state.update { featureToggles ->
            featureToggles.map {
                if (it.key == key) {
                    cachedToggleValues[key] = value
                    it.copy(value = value)
                } else {
                    it
                }
            }
        }
    }
    
}
