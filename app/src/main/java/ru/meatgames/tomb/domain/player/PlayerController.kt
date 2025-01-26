package ru.meatgames.tomb.domain.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.Component
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.StatusComponent
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.status.Status
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterController @Inject constructor() {

    private val _playerStateFlow = MutableStateFlow(
        PlayerState(
            position = PositionComponent(-1, -1),
        ),
    )
    val playerStateFlow: StateFlow<PlayerState> = _playerStateFlow
    val playerStateSnapshot: PlayerState
        get() = _playerStateFlow.value

    fun setPosition(
        coordinates: Coordinates,
    ) {
        _playerStateFlow.update { state ->
            state.updateComponent<PositionComponent> {
                coordinates.toPositionComponent()
            }
        }
    }

    fun move(
        direction: Direction,
    ) {
        _playerStateFlow.update { state ->
            state.updateComponent<PositionComponent> {
                it + direction.resolvedOffset
            }
        }
    }
    
    fun addItem(
        item: Item,
    ) {
        _playerStateFlow.update { state ->
            state.copy(
                inventory = state.inventory + item,
            )
        }
    }

    fun modifyHealth(
        modifier: Int,
    ) {
        _playerStateFlow.update { state ->
            state.updateComponent<HealthComponent> {
                it.updateHealth(modifier)
            }
        }
    }

    fun addStatus(
        status: Status,
    ) {
        _playerStateFlow.update { state ->
            state.updateComponent<StatusComponent> {
                it.add(status)
            }
        }
    }

    fun removeStatus(
        status: Status,
    ) {
        _playerStateFlow.update { state ->
            state.updateComponent<StatusComponent> {
                it.remove(status)
            }
        }
    }
    
}

data class PlayerState(
    val components: Set<Component>,
    val initiative: Initiative = Initiative.Medium,
    val inventory: List<Item> = emptyList(),
) {

    constructor(
        position: PositionComponent,
        health: HealthComponent = HealthComponent(10),
        status: StatusComponent = StatusComponent(emptySet()),
        initiative: Initiative = Initiative.Medium,
        inventory: List<Item> = emptyList<Item>(),
    ) : this(
        components = setOf(
            position,
            health,
            status,
        ),
        initiative = initiative,
        inventory = inventory,
    )

    inline fun <reified C : Component> updateComponent(
        crossinline update: (C) -> C,
    ): PlayerState {
        val component = getComponent<C>()
        val updatedComponent = update(component)

        return copy(
            components = components - component + updatedComponent,
        )
    }

    inline fun <reified C : Component> getComponent(): C {
        return components.filterIsInstance<C>().first()
    }

}
