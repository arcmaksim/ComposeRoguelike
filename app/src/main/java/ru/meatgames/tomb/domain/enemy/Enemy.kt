package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.domain.component.AttackComponent
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.PositionComponent
import java.util.UUID
import javax.inject.Inject

@JvmInline
value class EnemyId(val id: UUID = UUID.randomUUID())

data class Enemy @Inject constructor(
    val id: EnemyId = EnemyId(),
    val type: EnemyType,
    val health: HealthComponent,
    val position: PositionComponent,
    //val attack: AttackComponent,
    val initiative: Initiative,
)
