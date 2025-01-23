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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.meatgames.tomb.domain.DialogState
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.screen.compose.WinScreen
import ru.meatgames.tomb.screen.compose.charactersheet.CharacterSheetScreen
import ru.meatgames.tomb.screen.compose.featuretoggle.FeatureToggleScreen
import ru.meatgames.tomb.screen.compose.game.DeathScreen
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
        viewModel.dialogState.onEach { dialogState ->
            when (dialogState) {
                is DialogState.Container -> {
                    navController.safeNavigate("ContainerDialog/${dialogState.itemContainerId.id}")
                }
                
                is DialogState.GameMenu -> {
                    navController.safeNavigate(Scene.GameScreenDialog.id)
                }
                
                else -> Unit
            }
        }.launchIn(this)

        viewModel.scenes.onEach {
            viewModel.finishCurrentAnimations()
            navController.navigate(it.scene.id) {
                if (it.popUpToTop) popUpToTop(navController)
            }
        }.launchIn(this)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .displayCutoutPadding(),
    ) {
        NavHost(
            navController = navController,
            startDestination = Scene.MainMenu.id,
        ) {
            composable(Scene.MainMenu.id) {
                MainMenuScreen(
                    onCloseApp = onCloseApp,
                )
            }
            composable(Scene.MainGame.id) {
                GameScreen()
            }
            composable(Scene.WinScreen.id) {
                WinScreen(
                    onNavigateToMainMenu = {
                        navController.navigate(Scene.MainMenu.id) {
                            popUpToTop(navController)
                        }
                    },
                )
            }
            composable(Scene.DeathScreen.id) {
                DeathScreen(
                    onNavigateToMainMenu = {
                        navController.navigate(Scene.MainMenu.id) {
                            popUpToTop(navController)
                        }
                    },
                )
            }
            composable(Scene.Inventory.id) {
                InventoryScreen(
                    onBack = navController::navigateUp,
                )
            }
            composable(Scene.Stats.id) {
                CharacterSheetScreen(
                    onBack = navController::navigateUp,
                )
            }
            composable(Scene.FeatureToggles.id) {
                FeatureToggleScreen(
                    onBack = navController::navigateUp,
                )
            }
            dialog(Scene.GameScreenDialog.id) {
                GameScreenDialog(
                    onFeatureToggles = {
                        navController.navigateTo(
                            rootVM = viewModel,
                            state = Scene.FeatureToggles,
                        )
                    },
                    closeDialog = navController::navigateUp,
                    closeGame = {
                        viewModel.closeDialog()
                        onCloseApp()
                    },
                )
            }
            dialog(Scene.ContainerDialog.id) {
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
    state: Scene,
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
