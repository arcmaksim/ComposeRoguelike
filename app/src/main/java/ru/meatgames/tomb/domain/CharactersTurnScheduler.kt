package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.enemy.Enemy
import javax.inject.Inject

class CharactersTurnScheduler @Inject constructor() {

    fun produceSchedule(
        characterState: CharacterState,
        enemies: List<Enemy>,
    ): List<InitiativePosition> {
        val map = enemies.groupBy { it.initiative }
    
        return listOf(
            Initiative.SuperHigh, Initiative.High, Initiative.Medium, Initiative.Low, Initiative.SuperLow
        ).flatMap {  currentInitiative ->
            val enemiesPositions = map[currentInitiative]?.map(InitiativePosition::Enemy) ?: emptyList()
            val playerPosition = characterState.takeIf { it.initiative == currentInitiative }
                ?.let { InitiativePosition.Player }
            listOfNotNull(playerPosition) + enemiesPositions
        }
    }
    
    sealed class InitiativePosition {
        
        object Player : InitiativePosition()
        
        data class Enemy(val enemy: ru.meatgames.tomb.domain.enemy.Enemy) : InitiativePosition()
        
    }
    
}
