package ru.meatgames.tomb.screen.game.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.item.Item
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.container.ContainerItem

@Preview
@Composable
fun ContainerDialogPreview() {
    ContainerDialogContent(
        itemContainerId = ItemContainerId(),
        items = listOf(
            Item(name = "First item"),
            Item(name = "Second item"),
        ),
        onItemClick = { _, _ -> Unit },
        onDismissRequest = { Unit },
    )
}

@Composable
fun ContainerDialog(
    itemContainerId: ItemContainerId,
    viewModel: ContainerDialogVM = hiltViewModel(),
    closeDialog: () -> Unit,
) {
    LaunchedEffect(viewModel, itemContainerId) {
        viewModel.loadState(itemContainerId)
    }
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ContainerDialogEvent.CloseDialog -> closeDialog()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    ContainerDialogContent(
        itemContainerId = itemContainerId,
        items = state?.items?.toList() ?: emptyList(),
        onItemClick = viewModel::takeItem,
        onDismissRequest = viewModel::closeDialog,
    )
}

@Composable
private fun ContainerDialogContent(
    itemContainerId: ItemContainerId,
    items: List<Item>,
    onItemClick: (ItemContainerId, ItemId) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .heightIn(0.dp, 400.dp)
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Container",
                style = h2TextStyle,
            )
        
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn {
                items(items) { item ->
                    ContainerItem(
                        item = item,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        onItemClick(
                            itemContainerId,
                            it.id,
                        )
                    }
                }
            }
        }
    }
}
