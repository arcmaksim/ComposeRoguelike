package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.meatgames.tomb.GameState
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.design.h1TextStyle

@Composable
fun MainMenuScreen(
    navController: NavController,
    onCloseApp: () -> Unit,
) = Box(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    Text(
        text = "Yet Another\nRoguelike",
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.Center),
        style = h1TextStyle,
    )
    BaseTextButton(
        title = "Новая игра",
        modifier = Modifier.align(Alignment.BottomStart),
    ) {
        navController.navigate(GameState.MainGame.id)
    }
    BaseTextButton(
        title = "Выход",
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = onCloseApp,
    )
}
