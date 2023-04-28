package ru.meatgames.tomb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import ru.meatgames.tomb.domain.DialogState
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.screen.compose.WinScreen
import ru.meatgames.tomb.screen.compose.charactersheet.CharacterSheetScreen
import ru.meatgames.tomb.screen.compose.featuretoggle.FeatureToggleScreen
import ru.meatgames.tomb.screen.compose.game.GameScreen
import ru.meatgames.tomb.screen.compose.game.container.ContainerDialog
import ru.meatgames.tomb.screen.compose.game.dialog.GameScreenDialog
import ru.meatgames.tomb.screen.compose.inventory.InventoryScreen
import ru.meatgames.tomb.screen.compose.mainmenu.MainMenuScreen
import java.util.UUID

@ExperimentalMaterialApi
@Composable
fun TombApp(
    viewModel: RootVM,
    onCloseApp: () -> Unit,
) {
    val navController = rememberNavController()
    
    LaunchedEffect(viewModel) {
        viewModel.dialogState.collect { dialogState ->
            when (dialogState) {
                is DialogState.Container -> {
                    navController.safeNavigate("ContainerDialog/${dialogState.itemContainerId.id}")
                }
                is DialogState.GameMenu -> {
                    navController.safeNavigate(GameState.GameScreenDialog.id)
                }
                else -> Unit
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .displayCutoutPadding(),
    ) {
        NavHost(navController = navController, startDestination = GameState.MainMenu.id) {
            composable(GameState.MainMenu.id) {
                MainMenuScreen(
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
                    onBack = navController::navigateUp,
                )
            }
            composable(GameState.Stats.id) {
                CharacterSheetScreen(
                    onBack = navController::navigateUp,
                )
            }
            composable(GameState.FeatureToggles.id) {
                FeatureToggleScreen(
                    onBack = navController::navigateUp,
                )
            }
            dialog(GameState.GameScreenDialog.id) {
                GameScreenDialog(
                    onFeatureToggles = {
                        navController.navigateTo(
                            rootVM = viewModel,
                            state = GameState.FeatureToggles,
                        )
                    },
                    closeDialog = navController::navigateUp,
                    closeGame = onCloseApp,
                )
            }
            dialog(GameState.ContainerDialog.id) {
                ContainerDialog(
                    itemContainerId = ItemContainerId(
                        UUID.fromString(it.arguments!!.getString("itemContainerId")!!),
                    ),
                    closeDialog = navController::navigateUp,
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

private fun NavController.safeNavigate(
    destinationRoute: String,
) {
    runCatching { getBackStackEntry(destinationRoute) }
        .onFailure { navigate(destinationRoute) }
}
