package ru.meatgames.tomb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
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
import androidx.navigation.NavHostController
import ru.meatgames.tomb.R

@Composable
fun MainMenuCompose(
        navHostController: NavHostController
) {
    MainMenuContent(navHostController)
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun MainMenuContent(
        navHostController: NavHostController
) = Box(
        modifier = Modifier.background(Color(0x212121)).fillMaxSize()
) {
    BasicText(
            text = "Yet Another\nRoguelike",
            modifier = Modifier.padding(16.dp).align(Alignment.Center),
            style = TextStyle(
                    fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
            )
    )
    BasicText(
            text = "Новая игра",
            modifier = Modifier.padding(16.dp).align(Alignment.BottomStart).clickable(onClick = { Unit }),
            style = TextStyle(
                    fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
            )
    )
    BasicText(
            text = "Выход",
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd).clickable(onClick = { Unit }),
            style = TextStyle(
                    fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
            )
    )
}