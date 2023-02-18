package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.design.h2TextStyle

@Preview
@Composable
private fun GameScreenLoadingPreview() {
    GameScreenLoading()
}

@Composable
internal fun GameScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Loading...",
            style = h2TextStyle,
        )
    }
}
