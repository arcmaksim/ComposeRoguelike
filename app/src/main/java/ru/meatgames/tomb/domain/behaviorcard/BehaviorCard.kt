package ru.meatgames.tomb.domain.behaviorcard

import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

data class BehaviorCard(
    val title: String,
    val power: Power? = null,
    val speed: Speed? = null,
    val cunning: Cunning? = null,
    val technique: Technique? = null,
)
