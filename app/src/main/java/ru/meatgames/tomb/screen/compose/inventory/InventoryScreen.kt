package ru.meatgames.tomb.screen.compose.inventory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.screen.compose.system.Toolbar

@Preview(widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun InventoryScreenPreview() {
    InventoryScreenContent(
        state = InventoryState(emptyList()),
        onBack = { Unit },
    )
}

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                InventoryEvent.Back -> onBack()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsState()
    
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Items: ${state.items.size}",
                style = h3TextStyle,
                textAlign = TextAlign.Center,
            )
        }
    }
}
