package ru.meatgames.tomb.screen.game.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.PlayerInputProcessor
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.domain.item.ItemsHolder
import javax.inject.Inject

@HiltViewModel
class ContainerDialogVM @Inject constructor(
    private val gameController: GameController,
    private val itemsHolder: ItemsHolder,
    private val playerInputProcessor: PlayerInputProcessor,
): ViewModel() {
    
    private val _events = Channel<ContainerDialogEvent?>()
    val events: Flow<ContainerDialogEvent?> = _events.receiveAsFlow()
    
    private val _state = MutableStateFlow<State?>(null)
    val state: StateFlow<State?> = _state
    
    fun loadState(
        itemContainerId: ItemContainerId,
    ) {
        viewModelScope.launch {
            _state.value = State(
                itemContainerId = itemContainerId,
                items = itemsHolder.getItems(itemContainerId),
            )
        }
    }
    
    fun takeItem(
        itemContainerId: ItemContainerId,
        itemId: ItemId,
    ) {
        viewModelScope.launch {
            dismissDialog()
            playerInputProcessor.processPlayerInput(itemContainerId, itemId)
        }
    }
    
    fun closeDialog() {
        viewModelScope.launch {
            dismissDialog()
        }
    }
    
    private suspend fun dismissDialog() {
        gameController.closeCurrentDialog()
        _events.send(ContainerDialogEvent.CloseDialog)
    }
    
}

data class State(
    val itemContainerId: ItemContainerId,
    val items: Set<Item>,
)
