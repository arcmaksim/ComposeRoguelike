package ru.meatgames.tomb.screen.compose.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import ru.meatgames.tomb.design.h1TextStyle
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.design.h3TextStyle

@Preview
@Composable
private fun StatsPreview() {
    Health(
        maxHealth = 25,
        currentHealth = 12,
    )
}

@Composable
internal fun Health(
    modifier: Modifier = Modifier,
    currentHealth: Int,
    maxHealth: Int,
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
                painter = painterResource(R.drawable.ic_heart),
                contentDescription = null,
                tint = Color.Black.copy(alpha = .6f),
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier.offset(y = 8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = "$currentHealth",
                    style = h1TextStyle,
                    color = Color.Red,
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = "/$maxHealth",
                    style = h2TextStyle,
                )
            }
            Text(
                text = "Health",
                style = h3TextStyle,
            )
        }
    }
}