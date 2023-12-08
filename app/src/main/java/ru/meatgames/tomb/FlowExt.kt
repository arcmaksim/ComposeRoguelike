package ru.meatgames.tomb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <In, Out> StateFlow<In>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: In) -> Out,
): StateFlow<Out> = map { mapper(it) }
    .stateIn(
        coroutineScope,
        SharingStarted.Eagerly,
        mapper(value)
    )
