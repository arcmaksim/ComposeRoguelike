package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.domain.status.Status

data class StatusComponent(
    val statuses: Set<Status>,
) : Component
