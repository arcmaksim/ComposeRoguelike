package ru.meatgames.tomb.domain

import ru.meatgames.tomb.Direction

sealed class PlayerMoveResult {

    data class Move(
        val direction: Direction
    ) : PlayerMoveResult()

    object Interaction : PlayerMoveResult()

    object Block : PlayerMoveResult()

    //Attack
}
