package ru.meatgames.tomb.design

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun BaseTextButtonPreview() {
    BaseTextButton(
        title = "Title",
        onClick = { Unit },
    )
}

@Composable
fun BaseTextButton(
    title: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = h3TextStyle,
    onClick: () -> Unit,
) = Button(
    modifier = modifier,
    elevation = null,
    colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
    ),
    onClick = onClick,
) {
    Text(
        text = title,
        style = textStyle,
    )
}
