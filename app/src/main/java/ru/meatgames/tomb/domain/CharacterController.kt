package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique
import ru.meatgames.tomb.resolvedOffsets
import ru.meatgames.tomb.screen.compose.charactersheet.alertnessBehaviorCardPreview
import ru.meatgames.tomb.screen.compose.charactersheet.mightBehaviorCardPreview
import ru.meatgames.tomb.screen.compose.charactersheet.resilienceBehaviorCardPreview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterController @Inject constructor() {

    private val _characterStateFlow = MutableStateFlow(CharacterState(-1, -1))
    val characterStateFlow: StateFlow<CharacterState> = _characterStateFlow

    fun setPosition(
        mapX: Int,
        mapY: Int,
    ) {
        _characterStateFlow.update {
            it.copy(
                mapX = mapX,
                mapY = mapY,
            )
        }
    }

    fun move(
        direction: Direction,
    ) {
        val (x, y) = direction.resolvedOffsets
        _characterStateFlow.update {
            it.copy(
                mapX = it.mapX + x,
                mapY = it.mapY + y,
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
    
}

data class CharacterState(
    val mapX: Int,
    val mapY: Int,
    val power: Power = Power(10),
    val speed: Speed = Speed(1),
    val cunning: Cunning = Cunning(1),
    val technique: Technique = Technique(8),
    val offenseBehaviorCard: BehaviorCard? = mightBehaviorCardPreview,
    val defenceBehaviorCard: BehaviorCard? = resilienceBehaviorCardPreview,
    val supportBehaviorCard: BehaviorCard? = alertnessBehaviorCardPreview,
    val allBehaviorCards: List<BehaviorCard> = emptyList(),
    val inventory: List<Item> = emptyList(),
)
