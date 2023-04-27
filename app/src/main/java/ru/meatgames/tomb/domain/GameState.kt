package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult

sealed class GameState {
    
    object Loading : GameState()
    
    object WaitingForInput : GameState()
    
    object ProcessingInput : GameState()
    
    data class AnimatingCharacter(
        val turnResult: PlayerTurnResult,
    ) : GameState()
    
    // Needs for clearing animations
    object PrepareForEnemies : GameState()
    
    object ProcessingEnemies : GameState()
    
    data class AnimatingEnemies(
        // TODO ordered set
        val results: List<EnemyTurnResult>,
    ) : GameState()
    
}
