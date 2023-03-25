package ru.meatgames.tomb.screen.compose.charactersheet

import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.component.StatsComponent

enum class CharacterSheetEvent {
    Back,
}

data class CharacterSheetState(
    val stats: StatsComponent,
    val offensiveBehaviorCard: BehaviorCard?,
    val defensiveBehaviorCard: BehaviorCard?,
    val supportBehaviorCard: BehaviorCard?,
)
