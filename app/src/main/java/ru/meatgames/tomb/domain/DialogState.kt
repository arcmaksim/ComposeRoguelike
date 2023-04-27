package ru.meatgames.tomb.domain

import ru.meatgames.tomb.domain.item.ItemContainerId

sealed class DialogState {
    
    abstract val route: String
    
    data class Container(
        val itemContainerId: ItemContainerId,
    ) : DialogState() {
        override val route: String = "itemContainer/$itemContainerId"
    }
    
    object GameMenu : DialogState() {
        override val route: String = "gameMenu"
    }
    
}

val DialogState.isInterruptable: Boolean
    get() = when (this) {
        DialogState.GameMenu -> false
        else -> true
    }
