package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.R
import ru.meatgames.tomb.screen.compose.system.Toolbar

@Preview(widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun InventoryScreenPreview() {
    CharacterSheetScreenContent(
        state = characterSheetStatePreview,
        onBack = { Unit },
    )
}

@Composable
fun CharacterSheetScreen(
    viewModel: CharacterSheetVM,
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CharacterSheetEvent.Back -> onBack()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsState()
    
    CharacterSheetScreenContent(
        state = state,
        onBack = viewModel::onBack,
    )
}

@Composable
private fun CharacterSheetScreenContent(
    state: CharacterSheetState,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Toolbar(
            title = "Character Sheet",
            navigationIconResId = R.drawable.ic_arrow_back,
            onNavigationIcon = onBack,
        )
        
        val scrollableState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .verticalScroll(scrollableState)
                .padding(horizontal = 8.dp),
        ) {
            Stats(
                power = state.power,
                speed = state.speed,
                cunning = state.cunning,
                technique = state.technique,
            )
            BehaviorCards(
                offenseBehaviorCard = state.offensiveBehaviorCard,
                defenceBehaviorCard = state.defensiveBehaviorCard,
                supportBehaviorCard = state.supportBehaviorCard,
            )
        }
    }
}