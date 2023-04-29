package ru.meatgames.tomb.screen.compose.game.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import ru.meatgames.tomb.design.component.BaseTextButton
import ru.meatgames.tomb.design.h2TextStyle

@Preview
@Composable
fun GameScreenDialogPreview() {
    GameScreenDialogContent(
        onNewMapRequested = { Unit },
        onFeatureToggles = { Unit },
        onCloseGame = { Unit },
        onDismissRequest = { Unit },
    )
}

@Composable
fun GameScreenDialog(
    viewModel: GameScreenDialogVM = hiltViewModel(),
    onFeatureToggles: () -> Unit,
    closeDialog: () -> Unit,
    closeGame: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameScreenDialogEvent.NavigateToFeatureToggles -> onFeatureToggles()
                GameScreenDialogEvent.CloseDialog -> closeDialog()
                else -> Unit
            }
        }
    }
    
    GameScreenDialogContent(
        onNewMapRequested = viewModel::generateNewMap,
        onFeatureToggles = viewModel::showFeatureToggles,
        onCloseGame = closeGame,
        onDismissRequest = viewModel::closeDialog,
    )
}

@Composable
private fun GameScreenDialogContent(
    onNewMapRequested: () -> Unit,
    onFeatureToggles: () -> Unit,
    onCloseGame: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Menu",
                style = h2TextStyle,
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            BaseTextButton(
                title = "Generate new map",
                onClick = onNewMapRequested,
            )
            
            BaseTextButton(
                title = "Feature toggles",
                onClick = onFeatureToggles,
            )
            
            BaseTextButton(
                title = "Close game",
                onClick = onCloseGame,
            )
        }
    }
}
