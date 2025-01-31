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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.meatgames.tomb.domain.DialogState
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.screen.compose.win.WinScreen
import ru.meatgames.tomb.screen.compose.charactersheet.CharacterSheetScreen
import ru.meatgames.tomb.screen.compose.featuretoggle.FeatureToggleScreen
import ru.meatgames.tomb.screen.compose.death.DeathScreen
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

        viewModel.navigationCommandFlow.onEach {
            viewModel.finishCurrentAnimations()
            when (it) {
                is ScenesNavigator.Command.NavigateBack -> navController.popBackStack()
                is ScenesNavigator.Command.NavigateTo -> {
                    navController.navigate(it.scene.id) {
                        if (it.popUpToRoot) popUpToRoot()
                    }
                }
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
            route = Scene.Root.id,
            startDestination = Scene.MainMenuRoot.id,
        ) {
            mainMenuGraph(onCloseApp)
            gameGraph(
                onCloseApp = onCloseApp,
                onCloseDialog = viewModel::closeDialog,
            )
        }
    }
}

private fun NavGraphBuilder.mainMenuGraph(
    onCloseApp: () -> Unit,
) {
    navigation(
        route = Scene.MainMenuRoot.id,
        startDestination = Scene.MainMenu.id,
        builder = {
            composable(Scene.MainMenu.id) {
                MainMenuScreen(
                    onCloseApp = onCloseApp,
                )
            }
        },
    )
}

private fun NavGraphBuilder.gameGraph(
    onCloseDialog: () -> Unit,
    onCloseApp: () -> Unit,
) {
    navigation(
        route = Scene.MainGameRoot.id,
        startDestination = Scene.MainGame.id,
        builder = {
            composable(Scene.MainGame.id) {
                GameScreen()
            }
            composable(Scene.WinScreen.id) {
                WinScreen()
            }
            composable(Scene.DeathScreen.id) {
                DeathScreen()
            }
            composable(Scene.Inventory.id) {
                InventoryScreen()
            }
            composable(Scene.Stats.id) {
                CharacterSheetScreen()
            }
            composable(Scene.FeatureToggles.id) {
                FeatureToggleScreen()
            }
            dialog(Scene.GameScreenDialog.id) {
                GameScreenDialog(
                    closeGame = {
                        onCloseDialog()
                        onCloseApp()
                    },
                )
            }
            dialog(Scene.ContainerDialog.id) {
                ContainerDialog(
                    itemContainerId = ItemContainerId(
                        UUID.fromString(it.arguments!!.getString("itemContainerId")!!),
                    ),
                )
            }
        },
    )
}

private fun NavController.safeNavigate(
    destinationRoute: String,
) {
    runCatching { getBackStackEntry(destinationRoute) }
        .onFailure { navigate(destinationRoute) }
}
