package ru.meatgames.tomb.screen.inventory

import Toolbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.backgroundColorInt
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.screen.container.ContainerItem

@Preview(
    widthDp = 320,
    heightDp = 640,
    showBackground = true,
    backgroundColor = backgroundColorInt,
)
@Composable
private fun InventoryScreenPreview() {
    InventoryScreenContent(
        state = InventoryState(emptyList()),
        onBack = { Unit },
    )
}

@Composable
fun InventoryScreen(
    onBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                InventoryEvent.Back -> onBack()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    InventoryScreenContent(
        state = state,
        onBack = viewModel::onBack,
    )
}

@Composable
fun InventoryScreenContent(
    state: InventoryState,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Toolbar(
            title = "Inventory",
            navigationIconResId = R.drawable.ic_arrow_back,
            onNavigationIcon = onBack,
        )
        if (state.items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.items) {
                    ContainerItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = it,
                        onClick = { _ -> Unit },
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(1f),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Empty",
                    style = h3TextStyle,
                )
            }
        }
    }
}
