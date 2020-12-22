package ru.meatgames.tomb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.meatgames.tomb.screen.MainMenuCompose

@Composable
fun TombApp(
) {
    Box(
            modifier = Modifier.background(Color(0x212121)).fillMaxSize()
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = GameState.MAIN_MENU.id) {
            composable(GameState.MAIN_MENU.id) {
                GameController.changeScreen2(GameState.MAIN_MENU)
                MainMenuCompose(navHostController = navController)
            }
        }
    }
}