package ru.meatgames.tomb.screen.compose.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.meatgames.tomb.design.BaseTextButton
import ru.meatgames.tomb.domain.item.Item

@Preview
@Composable
private fun ContainerItemPreview() {
    ContainerItem(
        item = Item("Preview"),
        onClick = { Unit },
    )
}

@Composable
internal fun ContainerItem(
    item: Item,
    modifier: Modifier = Modifier,
    onClick: (Item) -> Unit,
) {
    BaseTextButton(
        modifier = modifier.then(Modifier),
        title = item.name,
    ) {
        onClick(item)
    }
}
