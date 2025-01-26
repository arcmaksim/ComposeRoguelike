package ru.meatgames.tomb.screen.compose.charactersheet

import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.StatsComponent
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

internal val characterSheetStatePreview = CharacterSheetState(
    health = HealthComponent(
        currentHealth = 12,
        maxHealth = 25,
    ),
    stats = StatsComponent(
        power = Power(5),
        speed = Speed(3),
        cunning = Cunning(2),
        technique = Technique(4),
    ),
)
