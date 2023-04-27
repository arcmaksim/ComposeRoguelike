package ru.meatgames.tomb.domain.map

import ru.meatgames.tomb.domain.turn.PlayerTurnResult

sealed class MapScreenCharacterAnimations {
    
    data class Player(
        val turnResult: PlayerTurnResult,
    ) : MapScreenCharacterAnimations()
    
    data class Enemies(
        val animations: EnemiesAnimations,
    ) : MapScreenCharacterAnimations()
    
}
