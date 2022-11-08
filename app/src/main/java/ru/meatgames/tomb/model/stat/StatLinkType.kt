package ru.meatgames.tomb.model.stat

enum class StatLinkType(val type: String) {
    CHILD("child"),
    PARENT("parent"),
    OWNER("owner");

    companion object {
        @JvmStatic
        fun getValue(type: String): StatLinkType {
            return when (type) {
                CHILD.type -> CHILD
                PARENT.type -> PARENT
                OWNER.type -> OWNER
                else -> throw IllegalArgumentException("Unknown value $type")
            }
        }
    }
}
