package ru.meatgames.tomb.screen.compose.game.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.R
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.compose.container.ContainerItem
import ru.meatgames.tomb.screen.compose.game.GameScreenInteractionState
import ru.meatgames.tomb.screen.compose.system.Toolbar

@Preview
@Composable
private fun GameScreenContainerWindowPreview() {
    GameScreenContainerWindow(
        modifier = Modifier,
        interactionState = GameScreenInteractionState.SearchingContainer(
            coordinates = 0 to 0,
            itemContainerId = ItemContainerId(),
            items = setOf(
                Item(name = "First item"),
                Item(name = "Second item"),
            ),
        ),
        onClose = { Unit },
        onItemClick = { _, _, _ -> Unit },
    )
}

@Composable
internal fun GameScreenContainerWindow(
    modifier: Modifier,
    interactionState: GameScreenInteractionState.SearchingContainer,
    onClose: () -> Unit,
    onItemClick: (Coordinates, ItemContainerId, ItemId) -> Unit,
) {
    BackHandler(onBack = onClose)
    
    Column(
        modifier = modifier
            .then(Modifier)
            .background(Color(0xFF212121)),
    ) {
        Toolbar(
            title = "Container",
            navigationIconResId = R.drawable.ic_close,
            onNavigationIcon = onClose,
        )
        LazyColumn(
            modifier = modifier.then(Modifier.fillMaxWidth()),
        ) {
            items(interactionState.items.toList()) { item ->
                ContainerItem(
                    item = item,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    onItemClick(
                        interactionState.coordinates,
                        interactionState.itemContainerId,
                        it.id,
                    )
                }
            }
        }
    }
}
