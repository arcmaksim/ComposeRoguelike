package ru.meatgames.tomb.screen.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.meatgames.tomb.design.component.BaseTextButton
import ru.meatgames.tomb.design.h1TextStyle

@Preview
@Composable
private fun MainMenuScreenPreview() {
    MainMenuScreenContent(
        onNewGame = { Unit },
        onPlayground = { Unit },
        onCloseApp = { Unit },
    )
}

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onCloseApp: () -> Unit,
    viewModel: MainMenuScreenViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            event ?: return@collect
            when (event) {
                MainMenuScreenViewModel.Event.NewGame -> onNewGame()
                MainMenuScreenViewModel.Event.Exit -> onCloseApp()
            }
        }
    }
    
    MainMenuScreenContent(
        onNewGame = viewModel::launchNewGame,
        onPlayground = viewModel::lunchPlayground,
        onCloseApp = viewModel::exitGame,
    )
}

@Composable
private fun MainMenuScreenContent(
    onNewGame: () -> Unit,
    onPlayground: () -> Unit,
    onCloseApp: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Color(0xFF212121))
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Title(
                modifier = Modifier.padding(16.dp),
            )
            Buttons(
                onNewGame = onNewGame,
                onPlayground = onPlayground,
                onCloseApp = onCloseApp,
            )
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Yet Another\nRoguelike",
        modifier = modifier,
        style = h1TextStyle,
    )
}

@Composable
private fun Buttons(
    onNewGame: () -> Unit,
    onPlayground: () -> Unit,
    onCloseApp: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BaseTextButton(
            title = "New game",
            onClick = onNewGame,
        )
        BaseTextButton(
            title = "Playground",
            onClick = onPlayground,
        )
        BaseTextButton(
            title = "Exit",
            onClick = onCloseApp,
        )
    }
}
