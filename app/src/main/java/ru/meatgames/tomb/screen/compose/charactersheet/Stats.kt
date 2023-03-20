package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

@Preview
@Composable
private fun InventoryScreenPreview() {
    Stats(
        power = Power(5),
        speed = Speed(3),
        cunning = Cunning(2),
        technique = Technique(4),
    )
}

@Composable
internal fun Stats(
    power: Power,
    speed: Speed,
    cunning: Cunning,
    technique: Technique,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.CenterStart,
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
