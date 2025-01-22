package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.domain.Coordinates

data class GoalComponent(
    val activeGoal: Goal? = null,
) : Component {

    sealed class Goal {
        data object Player : Goal()

        data class Position(
            val position: Coordinates,
        ) : Goal()
    }

}
