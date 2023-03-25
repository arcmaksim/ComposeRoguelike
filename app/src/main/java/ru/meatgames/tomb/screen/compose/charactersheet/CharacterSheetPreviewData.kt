package ru.meatgames.tomb.screen.compose.charactersheet

import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.component.StatsComponent
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

internal val mightBehaviorCardPreview = BehaviorCard(
    title = "Might",
    power = Power(5),
    technique = Technique(2),
)

internal val resilienceBehaviorCardPreview = BehaviorCard(
    title = "Resilience",
    power = Power(4),
    technique = Technique(1),
)

internal val alertnessBehaviorCardPreview = BehaviorCard(
    title = "Alertness",
    technique = Technique(4),
)

internal val characterSheetStatePreview = CharacterSheetState(
    stats = StatsComponent(
        power = Power(5),
        speed = Speed(3),
        cunning = Cunning(2),
        technique = Technique(4),
    ),
    offensiveBehaviorCard = mightBehaviorCardPreview,
    defensiveBehaviorCard = resilienceBehaviorCardPreview,
    supportBehaviorCard = alertnessBehaviorCardPreview,
)
