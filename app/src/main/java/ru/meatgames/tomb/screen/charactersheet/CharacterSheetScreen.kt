package ru.meatgames.tomb.screen.charactersheet

import Toolbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.backgroundColorInt

@Preview(
    widthDp = 320,
    heightDp = 640,
    showBackground = true,
    backgroundColor = backgroundColorInt,
)
@Composable
private fun InventoryScreenPreview() {
    CharacterSheetScreenContent(
        state = characterSheetStatePreview,
        onBack = { Unit },
    )
}

@Composable
fun CharacterSheetScreen(
    viewModel: CharacterSheetVM = hiltViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                CharacterSheetEvent.Back -> onBack()
                else -> Unit
            }
        }
    }
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Health(
                currentHealth = state.health.currentHealth,
                maxHealth = state.health.maxHealth,
            )
            Stats(
                power = state.stats.power,
                speed = state.stats.speed,
                cunning = state.stats.cunning,
                technique = state.stats.technique,
            )
            BehaviorCards(
                offenseBehaviorCard = state.offensiveBehaviorCard,
                defenceBehaviorCard = state.defensiveBehaviorCard,
                supportBehaviorCard = state.supportBehaviorCard,
            )
        }
    }
}
