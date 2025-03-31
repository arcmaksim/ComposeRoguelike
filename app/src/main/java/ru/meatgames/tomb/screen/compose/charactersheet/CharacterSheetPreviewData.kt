package ru.meatgames.tomb.screen.compose.charactersheet

import ru.meatgames.tomb.domain.component.HealthComponent

internal val characterSheetStatePreview = CharacterSheetState(
    health = HealthComponent(
        currentHealth = 12,
        maxHealth = 25,
    ),
)
