package ru.meatgames.tomb.config

data class FeatureToggleState(
    val key: FeatureToggleKey,
    val title: String,
    val value: Boolean,
)
