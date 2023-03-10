package ru.meatgames.tomb.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ru.meatgames.tomb.R

val fontFamily = FontFamily(
    fonts = listOf(
        Font(R.font.bulgaria_glorious_cyr),
    ),
)

private val baseTextStyle: TextStyle
    get() = TextStyle(
        fontFamily = fontFamily,
        color = Color.White,
        textAlign = TextAlign.Center,
    )

val h1TextStyle = baseTextStyle.copy(
    fontSize = 32.sp,
)

val h2TextStyle = baseTextStyle.copy(
    fontSize = 24.sp,
)

val h3TextStyle = baseTextStyle.copy(
    fontSize = 16.sp,
)
