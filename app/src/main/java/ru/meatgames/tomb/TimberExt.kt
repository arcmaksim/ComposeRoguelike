package ru.meatgames.tomb

import timber.log.Timber

fun logMessage(
    tag: String,
    message: String,
) {
    Timber.tag(tag)
    Timber.d(message)
}
