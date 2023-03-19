package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique
import ru.meatgames.tomb.screen.compose.system.Toolbar

@Preview(widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun InventoryScreenPreview() {
    CharacterSheetScreenContent(
        state = CharacterSheetState(
            power = Power(5),
            speed = Speed(3),
            cunning = Cunning(2),
            technique = Technique(4),
        ),
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
    
        Stats(
            power = state.power,
            speed = state.speed,
            cunning = state.cunning,
            technique = state.technique,
        )
    }
}

@Composable
private fun Stats(
    power: Power,
    speed: Speed,
    cunning: Cunning,
    technique: Technique,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Stat(
                statValue = power.value,
                statName = "Power",
            )
            Stat(
                statValue = speed.value,
                statName = "Speed",
            )
            Stat(
                statValue = cunning.value,
                statName = "Cunning",
            )
            Stat(
                statValue = technique.value,
                statName = "Technique",
            )
        }
    }
    
    
}

@Composable
private fun Stat(
    statValue: Int,
    statName: String,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = "$statValue",
            style = h1TextStyle,
        )
        Text(
            modifier = Modifier.alignByBaseline(),
            text = "$statName",
            style = h3TextStyle,
        )
    }
}
