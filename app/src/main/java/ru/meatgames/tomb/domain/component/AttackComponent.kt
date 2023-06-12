package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.enemy.EnemyId

data class AttackComponent(
    val targets: List<Attack>,
)

data class Attack(
    val damage: Int,
    val delay: Int,
)

data class AttackInstance(
    val enemyId: EnemyId,
    val target: Coordinates,
    val attack: Attack,
    val countdown: Int,
)
