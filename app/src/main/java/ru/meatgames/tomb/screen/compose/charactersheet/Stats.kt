package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.cunningColor
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.design.powerColor
import ru.meatgames.tomb.design.speedColor
import ru.meatgames.tomb.design.techniqueColor
import ru.meatgames.tomb.domain.stat.Cunning
import ru.meatgames.tomb.domain.stat.Power
import ru.meatgames.tomb.domain.stat.Speed
import ru.meatgames.tomb.domain.stat.Technique

@Preview
@Composable
private fun StatsPreview() {
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
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Stat(
                    iconResId = R.drawable.ic_power,
                    statValue = power.value,
                    statName = "Power",
                    color = powerColor,
                )
                Stat(
                    iconResId = R.drawable.ic_cunning,
                    statValue = cunning.value,
                    statName = "Cunning",
                    color = cunningColor,
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
    
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Stat(
                    iconResId = R.drawable.ic_speed,
                    statValue = speed.value,
                    statName = "Speed",
                    color = speedColor,
                )
                Stat(
                    iconResId = R.drawable.ic_technique,
                    statValue = technique.value,
                    statName = "Technique",
                    color = techniqueColor,
                )
            }
        }
    }
}

@Composable
private fun Stat(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    color: Color,
    statValue: Int,
    statName: String,
) {
    Box(
        modifier = modifier.then(
            Modifier.size(width = 104.dp, height = 96.dp),
        ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = Color.Black.copy(alpha = .6f),
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "$statValue",
                style = h1TextStyle,
                color = color,
            )
            Text(
                text = statName,
                style = h3TextStyle,
            )
        }
    }
}
