package ru.meatgames.tomb.domain.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.component.Component
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.StatsComponent
import ru.meatgames.tomb.domain.component.StatusComponent
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique
import ru.meatgames.tomb.domain.status.Status
import ru.meatgames.tomb.resolvedOffset
import ru.meatgames.tomb.screen.compose.charactersheet.alertnessBehaviorCardPreview
import ru.meatgames.tomb.screen.compose.charactersheet.mightBehaviorCardPreview
import ru.meatgames.tomb.screen.compose.charactersheet.resilienceBehaviorCardPreview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterController @Inject constructor() {

    private val _characterStateFlow = MutableStateFlow(
        CharacterState(
            position = PositionComponent(-1, -1),
        ),
    )
    val characterStateFlow: StateFlow<CharacterState> = _characterStateFlow
    val characterState: CharacterState
        get() = _characterStateFlow.value

    fun setPosition(
        coordinates: Coordinates,
    ) {
        _characterStateFlow.update { state ->
            state.updateComponent<PositionComponent> {
                coordinates.toPositionComponent()
            }
        }
    }

    fun move(
        direction: Direction,
    ) {
        _characterStateFlow.update { state ->
            state.updateComponent<PositionComponent> {
                it + direction.resolvedOffset
            }
        }
    }
    
    fun addItem(
        item: Item,
    ) {
        _characterStateFlow.update { state ->
            state.copy(
                inventory = state.inventory + item,
            )
        }
    }

    fun modifyHealth(
        modifier: Int,
    ) {
        _characterStateFlow.update { state ->
            state.updateComponent<HealthComponent> {
                it.updateHealth(modifier)
            }
        }
    }

    fun addStatus(
        status: Status,
    ) {
        _characterStateFlow.update { state ->
            state.updateComponent<StatusComponent> {
                it.add(status)
            }
        }
    }

    fun removeStatus(
        status: Status,
    ) {
        _characterStateFlow.update { state ->
            state.updateComponent<StatusComponent> {
                it.remove(status)
            }
        }
    }
    
}

data class CharacterState(
    val components: Set<Component>,
    val initiative: Initiative = Initiative.Medium,
    val offenseBehaviorCard: BehaviorCard? = mightBehaviorCardPreview,
    val defenceBehaviorCard: BehaviorCard? = resilienceBehaviorCardPreview,
    val supportBehaviorCard: BehaviorCard? = alertnessBehaviorCardPreview,
    val allBehaviorCards: List<BehaviorCard> = emptyList(),
    val inventory: List<Item> = emptyList(),
) {

    constructor(
        position: PositionComponent,
        health: HealthComponent = HealthComponent(10),
        status: StatusComponent = StatusComponent(emptySet()),
        initiative: Initiative = Initiative.Medium,
        stats: StatsComponent = StatsComponent(
            power = Power(10),
            speed = Speed(1),
            cunning = Cunning(1),
            technique = Technique(8),
        ),
        inventory: List<Item> = emptyList<Item>(),
    ) : this(
        components = setOf(
            position,
            health,
            stats,
            status,
        ),
        initiative = initiative,
        offenseBehaviorCard = mightBehaviorCardPreview,
        defenceBehaviorCard = resilienceBehaviorCardPreview,
        supportBehaviorCard = alertnessBehaviorCardPreview,
        allBehaviorCards = emptyList(),
        inventory = inventory,
    )

    inline fun <reified C : Component> updateComponent(
        crossinline update: (C) -> C,
    ): CharacterState {
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
