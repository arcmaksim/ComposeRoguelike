package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.domain.component.Component
import ru.meatgames.tomb.domain.component.GoalComponent
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.PositionComponent
import java.util.UUID

@JvmInline
value class EnemyId(val id: UUID = UUID.randomUUID())

class Enemy(
    val id: EnemyId = EnemyId(),
    val type: EnemyType,
    val initiative: Initiative,
    val components: MutableSet<Component>,
) {

    constructor(
        id: EnemyId = EnemyId(),
        type: EnemyType,
        health: HealthComponent,
        position: PositionComponent,
        initiative: Initiative,
        goal: GoalComponent,
    ) : this (
        id = id,
        type = type,
        initiative = initiative,
        components = mutableSetOf(
            health,
            position,
            goal,
        ),
    )

    inline fun <reified C : Component> updateComponent(
        crossinline update: (C) -> C,
    ): C {
        val component = components.filterIsInstance<C>().first()
        components.remove(component)

        val updatedComponent = update(component)
        components.add(updatedComponent)

        return updatedComponent
    }

    inline fun <reified C : Component> getComponent(): C {
        return components.filterIsInstance<C>().first()
    }

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is Enemy) return false
        return id == other.id
    }

}
