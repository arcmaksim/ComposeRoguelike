package ru.meatgames.tomb.screen.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.meatgames.tomb.R

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun StubScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BasicText(
            text = "Stub",
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontFamily = fontFamily(font(R.font.bulgaria_glorious_cyr)),
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}