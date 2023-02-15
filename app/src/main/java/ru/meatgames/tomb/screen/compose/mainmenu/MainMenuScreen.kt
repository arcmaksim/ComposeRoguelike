package ru.meatgames.tomb.screen.compose.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.design.h1TextStyle

@Composable
fun MainMenuScreen(
    viewModel: MainMenuScreenViewModel,
    onNewGame: () -> Unit,
    onCloseApp: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            event ?: return@collect
            when (event) {
                MainMenuScreenViewModel.Event.NewGame -> onNewGame()
                MainMenuScreenViewModel.Event.Exit -> onCloseApp()
            }
        }
    }
    
    Box(
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
            title = "New game",
            modifier = Modifier.align(Alignment.BottomStart),
            onClick = viewModel::newGame,
        )
        BaseTextButton(
            title = "Exit",
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = viewModel::exitGame,
        )
    }
}
