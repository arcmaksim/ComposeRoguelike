package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.player.CharacterState

data class ExperimentalGameState(
    val character: CharacterState,
    val items: List<ItemId>,
    val enemies: List<EnemyId>,
    val screen: Screen,
)

sealed class Screen {
    
    object MainMenu : Screen()
    
    data class Map(
        val tilesToRender: List<Any?>,
        val animations: Any?, // either player or enemies
    ) : Screen()
    
}
