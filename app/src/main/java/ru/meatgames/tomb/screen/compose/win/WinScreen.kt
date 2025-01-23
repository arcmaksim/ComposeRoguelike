package ru.meatgames.tomb.screen.compose.win

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.meatgames.tomb.design.component.BaseTextButton
import ru.meatgames.tomb.design.h1TextStyle

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun WinScreenPreview() {
    WinScreen()
}

@Composable
fun WinScreen(
    viewModel: WinScreenVM = hiltViewModel(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121)),
    ) {
        Text(
            text = "You won!",
            modifier = Modifier.align(Alignment.Center),
            style = h1TextStyle,
        )

        BaseTextButton(
            title = "To main menu",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onClick = viewModel::navigateToMainMenu,
        )
    }
}