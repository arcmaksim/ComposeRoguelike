package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.meatgames.tomb.GameState
import ru.meatgames.tomb.R

private val buttonPadding = 16.dp

val fontFamily = FontFamily(
    fonts = listOf(
        Font(R.font.bulgaria_glorious_cyr),
    ),
)

private val buttonTextStyle = TextStyle(
    fontFamily = fontFamily,
    fontSize = 12.sp,
    color = Color.White,
    textAlign = TextAlign.Center,
)


@Composable
fun MainMenuScreen(
    navController: NavController,
) = Box(
    modifier = Modifier
        .background(Color(0xFF212121))
        .fillMaxSize(),
) {
    Text(
        text = "Yet Another\nRoguelike",
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.Center),
        style = TextStyle(
            fontFamily = fontFamily,
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        ),
    )
    MainMenuButton(
        title = "Новая игра",
        modifier = Modifier.align(Alignment.BottomStart),
    ) {
        navController.navigate(GameState.MainGame.id)
    }
    MainMenuButton(
        title = "Выход",
        modifier = Modifier.align(Alignment.BottomEnd),
    ) {
        navController.navigate(GameState.Stub.id)
    }
}

@Composable
private fun MainMenuButton(
    title: String,
    modifier: Modifier,
    onClick: () -> Unit,
) = Button(
    modifier = modifier.padding(buttonPadding),
    elevation = null,
    colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
    ),
    onClick = onClick,
) {
    Text(title, style = buttonTextStyle)
}
