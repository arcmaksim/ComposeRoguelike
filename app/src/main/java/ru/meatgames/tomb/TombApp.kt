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
import ru.meatgames.tomb.screen.compose.mainmenu.MainMenuScreen
import ru.meatgames.tomb.screen.compose.WinScreen
import ru.meatgames.tomb.screen.compose.game.GameScreen
import ru.meatgames.tomb.screen.compose.inventory.InventoryScreen

@ExperimentalMaterialApi
@Composable
fun TombApp(
    onCloseApp: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Color(0xFF212121))
            .fillMaxSize(),
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = GameState.MainMenu.id) {
            composable(GameState.MainMenu.id) {
                MainMenuScreen(
                    viewModel = hiltViewModel(),
                    onNewGame = { navController.navigate(GameState.MainGame.id) },
                    onCloseApp = onCloseApp,
                )
            }
            composable(GameState.MainGame.id) {
                GameScreen(
                    viewModel = hiltViewModel(),
                    onWin = { navController.navigate(GameState.WinScreen.id) },
                    onInventory = { navController.navigate(GameState.Inventory.id) },
                )
            }
            composable(GameState.WinScreen.id) {
                WinScreen(
                    onNavigateToMainMenu = { navController.navigate(GameState.MainMenu.id) },
                )
            }
            composable(GameState.Inventory.id) {
                InventoryScreen(
                    viewModel = hiltViewModel(),
                    onBack = { navController.navigate(GameState.MainGame.id) },
                )
            }
        }
    }
}
