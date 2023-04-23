package ru.meatgames.tomb.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FeatureToggles {
    
    private val _state = MutableStateFlow(
        listOf(
            FeatureToggleState(
                key = FeatureToggleKey.SkipPlayerAnimations,
                title = "Skip player animations",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggleKey.SkipEnemiesAnimations,
                title = "Skip enemies animations",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggleKey.ShowEnemiesHealthBar,
                title = "Show enemies health bar",
                value = true,
            ),
        )
    )
    val state: StateFlow<List<FeatureToggleState>> = _state
    
    val themeOverride: String? = null
    
    private val cachedToggleValues: MutableMap<FeatureToggleKey, Boolean> = _state.value
        .associate { it.key to it.value }
        .toMutableMap()
    
    fun getToggleValue(
        key: FeatureToggleKey,
    ): Boolean = cachedToggleValues[key]!!
    
    fun updateToggle(
        key: FeatureToggleKey,
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
