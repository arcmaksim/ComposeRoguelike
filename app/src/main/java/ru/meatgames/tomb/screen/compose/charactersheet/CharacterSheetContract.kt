package ru.meatgames.tomb.screen.compose.charactersheet

import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

enum class CharacterSheetEvent {
    Back,
}

data class CharacterSheetState(
    val power: Power,
    val speed: Speed,
    val cunning: Cunning,
    val technique: Technique,
    val offensiveBehaviorCard: BehaviorCard?,
    val defensiveBehaviorCard: BehaviorCard?,
    val supportBehaviorCard: BehaviorCard?,
)
