package ru.meatgames.tomb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.meatgames.tomb.screen.compose.MainMenuScreen
import ru.meatgames.tomb.screen.compose.StubScreen
import ru.meatgames.tomb.screen.compose.game.GameScreen
import ru.meatgames.tomb.screen.compose.game.GameScreenViewModel

@ExperimentalMaterialApi
@Composable
fun TombApp() {
    Box(
        modifier = Modifier
            .background(Color(0xFF212121))
            .fillMaxSize(),
    ) {
        val navController = rememberNavController()
        val gameScreenViewModel: GameScreenViewModel = hiltViewModel()
        NavHost(navController = navController, startDestination = GameState.MainMenu.id) {
            composable(GameState.MainMenu.id) {
                MainMenuScreen(navController = navController)
            }
            composable(GameState.MainGame.id) {
                GameScreen(
                    gameScreenViewModel = gameScreenViewModel,
                    navController = navController,
                )
            }
            composable(GameState.Stub.id) {
                StubScreen()
            }
        }
    }
}
