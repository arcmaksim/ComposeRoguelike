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
import ru.meatgames.tomb.screen.compose.game.ThemedGameScreen
import ru.meatgames.tomb.screen.compose.game.ThemedGameScreenViewModel

@ExperimentalMaterialApi
@Composable
fun TombApp() {
    Box(
        modifier = Modifier
            .background(Color(0xFF212121))
            .fillMaxSize(),
    ) {
        val navController = rememberNavController()
        val gameScreenViewModel: ThemedGameScreenViewModel = hiltViewModel()
        NavHost(navController = navController, startDestination = GameState.MainMenu.id) {
            composable(GameState.MainMenu.id) {
                GameController.changeScreen2(GameState.MainMenu)
                MainMenuScreen(navController = navController)
            }
            composable(GameState.MainGame.id) {
                GameController.changeScreen2(GameState.MainGame)
                ThemedGameScreen(
                    gameScreenViewModel = gameScreenViewModel,
                    navController = navController,
                )
            }
        }
    }
}