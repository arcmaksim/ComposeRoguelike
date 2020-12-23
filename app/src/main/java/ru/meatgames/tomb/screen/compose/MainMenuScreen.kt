package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import ru.meatgames.tomb.GameState
import ru.meatgames.tomb.R

private val buttonPadding = 16.dp

@ExperimentalMaterialApi
private val buttonColors = object : ButtonColors {
    override fun backgroundColor(enabled: Boolean): Color = Color.Transparent
    override fun contentColor(enabled: Boolean): Color = Color.Transparent
}

private val buttonTextStyle = TextStyle(
    fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
    fontSize = 12.sp,
    color = Color.White,
    textAlign = TextAlign.Center
)


@ExperimentalMaterialApi
@Preview(widthDp = 360, heightDp = 640)
@Composable
fun MainMenuScreen(
    navController: NavController
) = Box(
    modifier = Modifier.background(Color(0x212121)).fillMaxSize()
) {
    Text(
        text = "Yet Another\nRoguelike",
        modifier = Modifier.padding(16.dp).align(Alignment.Center),
        style = TextStyle(
            fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
    MainMenuButton(
        title = "Новая игра",
        modifier = Modifier.align(Alignment.BottomStart)
    ) {
        navController.navigate(GameState.Stub.id)
    }
    MainMenuButton(
        title = "Выход",
        modifier = Modifier.align(Alignment.BottomEnd)
    ) {
        navController.navigate(GameState.Stub.id)
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainMenuButton(
    title: String,
    modifier: Modifier,
    onClick: () -> Unit
) = Button(
    modifier = modifier.padding(buttonPadding),
    elevation = null,
    colors = buttonColors,
    onClick = onClick
) {
    Text(title, style = buttonTextStyle)
}