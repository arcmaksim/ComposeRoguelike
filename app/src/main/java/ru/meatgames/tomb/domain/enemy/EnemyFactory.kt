package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.toPositionComponent

fun EnemyType.produceEnemy(
    position: Coordinates,
): Enemy = Enemy(
    type = this,
    position = position.toPositionComponent(),
    health = resolveHealthComponent(),
    initiative = resolveInitiative(),
)

private fun EnemyType.resolveHealthComponent(): HealthComponent = HealthComponent(
    maxHealth = when (this) {
        EnemyType.Skeleton -> 6
        EnemyType.SkeletonArcher -> 4
        EnemyType.SkeletonWarrior -> 8
        EnemyType.SkeletonNecromancer -> 3
    }
)

private fun EnemyType.resolveInitiative(): Initiative = when (this) {
    EnemyType.Skeleton -> Initiative.High
    EnemyType.SkeletonArcher -> Initiative.Medium
    EnemyType.SkeletonWarrior -> Initiative.Low
    EnemyType.SkeletonNecromancer -> Initiative.Medium
}
