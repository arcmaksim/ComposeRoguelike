package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.domain.status.Status

data class StatusComponent(
    val statuses: Set<Status> = emptySet<Status>(),
) : Component {

    fun has(
        status: Status,
    ): Boolean = statuses.contains(status)

    fun add(
        status: Status,
    ): StatusComponent = copy(
        statuses + setOf(status),
    )

    fun remove(
        status: Status,
    ): StatusComponent = copy(
        statuses + setOf(status),
    )

}
