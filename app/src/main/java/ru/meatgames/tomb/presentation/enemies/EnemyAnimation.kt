package ru.meatgames.tomb.presentation.enemies

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.render.RenderData

sealed class EnemyAnimation {

    data class Move(
        val direction: Direction,
        val fade: Fade,
    ) : EnemyAnimation() {
        enum class Fade { IN, OUT, NONE, }
    }
    
    data class Attack(
        val direction: Direction,
    ) : EnemyAnimation()
    
    data class Icon(
        val renderData: RenderData,
    ) : EnemyAnimation()

}
