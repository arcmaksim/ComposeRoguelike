package ru.meatgames.tomb.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FeatureToggles {
    
    private val _state = MutableStateFlow(
        listOf(
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
                value = false,
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
            FeatureToggleState(
                key = FeatureToggle.UndyingCharacter,
                title = "Game over screen won't be triggered",
                value = false,
            ),
            FeatureToggleState(
                key = FeatureToggle.RoundFov,
                title = "Rounds FOV to the circle shape",
                value = true,
            ),
            FeatureToggleState(
                key = FeatureToggle.DrawFogOfWar,
                title = "Enable drawing fog of war",
                value = true,
            ),
        )
    )
    val state: StateFlow<List<FeatureToggleState>> = _state
    
    var themeOverride: String? = null
    
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
