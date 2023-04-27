package ru.meatgames.tomb

import timber.log.Timber

fun logMessage(
    tag: String,
    message: String,
) {
    Timber.tag(tag)
    Timber.d(message)
}

fun String.logMessageWithTag(
    tag: String,
) {
    Timber.tag(tag)
    Timber.d(this)
}

fun String.logErrorWithTag(
    tag: String,
) {
    Timber.tag(tag)
    Timber.e(this)
}
