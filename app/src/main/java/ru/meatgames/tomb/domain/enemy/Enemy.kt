package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.toPositionComponent
import java.util.UUID
import javax.inject.Inject

@JvmInline
value class EnemyId(val id: UUID = UUID.randomUUID())

class Enemy @Inject constructor(
    val id: EnemyId = EnemyId(),
    val type: EnemyType,
    val health: HealthComponent,
    val position: PositionComponent,
) {
    
    constructor(
        id: EnemyId = EnemyId(),
        type: EnemyType,
        position: Coordinates,
    ) : this(
        id = id,
        type = type,
        health = HealthComponent(10),
        position = position.toPositionComponent(),
    )
    
}
