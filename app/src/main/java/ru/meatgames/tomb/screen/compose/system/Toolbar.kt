package ru.meatgames.tomb.screen.compose.system

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.R
import ru.meatgames.tomb.design.h2TextStyle

@Preview
@Composable
private fun ToolbarPreview() {
    Toolbar(
        title = "Toolbar",
        navigationIconResId = R.drawable.ic_arrow_back,
        onNavigationIcon = { Unit },
    )
}

@Composable
fun Toolbar(
    title: String,
    @DrawableRes navigationIconResId: Int,
    onNavigationIcon: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = onNavigationIcon,
        ) {
            Icon(
                painter = painterResource(navigationIconResId),
                contentDescription = "Toolbar navigation button",
                tint = Color.White,
            )
        }
        Text(
            modifier = Modifier.weight(1f, fill = true),
            text = title,
            textAlign = TextAlign.End,
            style = h2TextStyle,
        )
    }
}