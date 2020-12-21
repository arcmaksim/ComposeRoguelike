package ru.meatgames.tomb.screen

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class MainMenuCompose {

    private val titleTextStyle = TextStyle(
            Color.White,
            fontSize = 32.sp
    )
    private val buttonTextStyle = TextStyle(
            Color.White,
            fontSize = 12.sp
    )


    @Preview(widthDp = 360, heightDp = 640)
    @Composable
    fun asd() = Box(
            modifier = Modifier.background(Color(0x212121)).fillMaxSize()
    ) {
        Text(
                "Yet Another\nRoguelike",
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                style = titleTextStyle
        )
        Text(
                text = "Новая игра",
                modifier = Modifier.align(Alignment.BottomStart),
                style = buttonTextStyle
        )
        Text(
                text = "Выход",
                modifier = Modifier.align(Alignment.BottomEnd),
                style = buttonTextStyle
        )
    }

}