package ru.meatgames.tomb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import ru.meatgames.tomb.screen.compose.mainmenu.MainMenuScreen
import ru.meatgames.tomb.screen.compose.WinScreen
import ru.meatgames.tomb.screen.compose.charactersheet.CharacterSheetScreen
import ru.meatgames.tomb.screen.compose.featuretoggle.FeatureToggleScreen
import ru.meatgames.tomb.screen.compose.game.GameScreen
import ru.meatgames.tomb.screen.compose.game.dialog.GameScreenDialog
import ru.meatgames.tomb.screen.compose.inventory.InventoryScreen

@ExperimentalMaterialApi
@Composable
fun TombApp(
    viewModel: RootVM,
    onCloseApp: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .displayCutoutPadding(),
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = GameState.MainMenu.id) {
            composable(GameState.MainMenu.id) {
                MainMenuScreen(
                    viewModel = hiltViewModel(),
                    onNewGame = {
                        navController.navigate(GameState.MainGame.id) {
                            popUpToTop(navController)
                        }
                    },
                    onCloseApp = onCloseApp,
                )
            }
            composable(GameState.MainGame.id) {
                GameScreen(
                    viewModel = hiltViewModel(),
                    onWin = {
                        navController.navigate(GameState.WinScreen.id) {
                            popUpToTop(navController)
                        }
                    },
                    onInventory = {
                        navController.navigateTo(
                            rootVM = viewModel,
                            state = GameState.Inventory,
                        )
                    },
                    onCharacterSheet = {
                        navController.navigateTo(
                            rootVM = viewModel,
                            state = GameState.Stats,
                        )
                    },
                    onDialog = { navController.navigate(GameState.GameScreenDialog.id) },
                )
            }
            composable(GameState.WinScreen.id) {
                WinScreen(
                    onNavigateToMainMenu = {
                        navController.navigate(GameState.MainMenu.id) {
                            popUpToTop(navController)
                        }
                    },
                )
            }
            composable(GameState.Inventory.id) {
                InventoryScreen(
                    viewModel = hiltViewModel(),
                    onBack = navController::navigateUp,
                )
            }
            composable(GameState.Stats.id) {
                CharacterSheetScreen(
                    viewModel = hiltViewModel(),
                    onBack = navController::navigateUp,
                )
            }
            composable(GameState.FeatureToggles.id) {
                FeatureToggleScreen(
                    viewModel = hiltViewModel(),
                    onBack = navController::navigateUp,
                )
            }
            dialog(GameState.GameScreenDialog.id) {
                GameScreenDialog(
                    viewModel = hiltViewModel(),
                    closeDialog = navController::navigateUp,
                    onFeatureToggles = {
                        navController.navigateUp()
                        navController.navigateTo(
                            rootVM = viewModel,
                            state = GameState.FeatureToggles,
                        )
                    },
                    closeGame = onCloseApp,
                )
            }
        }
    }
}

private fun NavController.navigateTo(
    rootVM: RootVM,
    state: GameState,
) {
    rootVM.finishCurrentAnimations()
    navigate(state.id)
}
