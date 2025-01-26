package ru.meatgames.tomb.domain.turn

import ru.meatgames.tomb.domain.component.Initiative
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.player.PlayerState
import javax.inject.Inject

class CharactersTurnScheduler @Inject constructor() {

    fun produceSchedule(
        playerState: PlayerState,
        enemies: List<Enemy>,
    ): List<InitiativePosition> {
        val map = enemies.groupBy { it.initiative }
    
        return listOf(
            Initiative.SuperHigh, Initiative.High, Initiative.Medium, Initiative.Low, Initiative.SuperLow
        ).flatMap { currentInitiative ->
            val enemiesPositions = map[currentInitiative]?.map { InitiativePosition.Enemy(it.id) } ?: emptyList()
            val playerPosition = playerState.takeIf { it.initiative == currentInitiative }
                ?.let { InitiativePosition.Player }
            listOfNotNull(playerPosition) + enemiesPositions
        }
    }
    
    sealed class InitiativePosition {
        
        object Player : InitiativePosition()
        
        data class Enemy(
            val enemyId: EnemyId,
        ) : InitiativePosition()
        
    }
    
}
