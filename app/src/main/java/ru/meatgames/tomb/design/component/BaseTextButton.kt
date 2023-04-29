package ru.meatgames.tomb.design.component

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.design.h3TextStyle

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
) = TextButton(
    modifier = modifier,
    elevation = null,
    onClick = onClick,
) {
    Text(
        text = title,
        style = textStyle,
    )
}
