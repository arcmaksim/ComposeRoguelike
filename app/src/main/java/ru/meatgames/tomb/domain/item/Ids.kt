package ru.meatgames.tomb.domain.item

import java.util.UUID

@JvmInline
value class ItemId(val id: UUID = UUID.randomUUID())

@JvmInline
value class ItemContainerId(val id: UUID = UUID.randomUUID())
