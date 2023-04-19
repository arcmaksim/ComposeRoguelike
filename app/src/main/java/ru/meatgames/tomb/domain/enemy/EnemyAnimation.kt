package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.Direction

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

}
