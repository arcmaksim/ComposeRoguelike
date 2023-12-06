package ru.meatgames.tomb.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.design.h1TextStyle

@Preview(widthDp = 360, heightDp = 640,)
@Composable
fun StubScreenPreview() {
    StubScreen()
}

@Composable
fun StubScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121)),
    ) {
        Text(
            text = "Stub",
            modifier = Modifier.align(Alignment.Center),
            style = h1TextStyle,
        )
    }
}