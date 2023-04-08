package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.StatsComponent
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique
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

    fun setPosition(
        coordinates: Coordinates,
    ) {
        _characterStateFlow.update { state ->
            state.copy(
                position = coordinates.toPositionComponent(),
            )
        }
    }

    fun move(
        direction: Direction,
    ) {
        _characterStateFlow.update { state ->
            state.copy(
                position = state.position + direction.resolvedOffset,
            )
        }
    }
    
    fun addItem(
        item: Item,
    ) {
        _characterStateFlow.update {
            it.copy(
                inventory = it.inventory + item,
            )
        }
    }
    fun modifyHealth(
        modifier: Int,
    ) {
        _characterStateFlow.update {
            it.copy(
                health = it.health.updateHealth(modifier),
            )
        }
    }
    
}

data class CharacterState(
    val position: PositionComponent,
    val health: HealthComponent = HealthComponent(10),
    val initiative: Initiative = Initiative.Medium,
    val stats: StatsComponent = StatsComponent(
        power = Power(10),
        speed = Speed(1),
        cunning = Cunning(1),
        technique = Technique(8),
    ),
    val offenseBehaviorCard: BehaviorCard? = mightBehaviorCardPreview,
    val defenceBehaviorCard: BehaviorCard? = resilienceBehaviorCardPreview,
    val supportBehaviorCard: BehaviorCard? = alertnessBehaviorCardPreview,
    val allBehaviorCards: List<BehaviorCard> = emptyList(),
    val inventory: List<Item> = emptyList(),
)
