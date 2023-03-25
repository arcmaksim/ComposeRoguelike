package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

data class StatsComponent(
    val power: Power = Power(3),
    val speed: Speed = Speed(3),
    val cunning: Cunning = Cunning(3),
    val technique: Technique = Technique(3),
)
