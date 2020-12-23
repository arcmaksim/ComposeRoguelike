package ru.meatgames.tomb

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> LazyGrid(
    items: List<T> = listOf(),
    columns: Int,
    hPadding: Int = 0,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    val chunkedList = items.chunked(columns)
    LazyColumn(modifier = Modifier.padding(horizontal = hPadding.dp)) {
        itemsIndexed(items = chunkedList) { _, item ->
            Row {
                item.forEachIndexed { _, item ->
                    Box(modifier = Modifier.weight(1F).align(Alignment.Top),
                        contentAlignment = Alignment.Center
                    ) {
                        itemContent(item)
                    }
                }
                repeat(columns - item.size) {
                    Box(modifier = Modifier.weight(1F).padding(2.dp))
                }
            }
        }
    }
}