package ru.meatgames.tomb.design

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BaseTextButton(
    title: String,
    modifier: Modifier,
    onClick: () -> Unit,
) = Button(
    modifier = modifier.padding(16.dp),
    elevation = null,
    colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
    ),
    onClick = onClick,
) {
    Text(
        text = title,
        style = baseTextStyle.copy(
            fontSize = 12.sp,
        ),
    )
}