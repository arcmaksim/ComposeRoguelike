package ru.meatgames.tomb.config

data class FeatureToggleState(
    val key: FeatureToggle,
    val title: String,
    val value: Boolean,
)
