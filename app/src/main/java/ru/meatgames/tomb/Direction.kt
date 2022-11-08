package ru.meatgames.tomb

enum class Direction {
    Left,
    Right,
    Top,
    Bottom,
}

val Direction.resolvedOffsets: Pair<Int, Int>
    get() = when (this) {
        Direction.Left -> -1 to 0
        Direction.Right -> 1 to 0
        Direction.Top -> 0 to -1
        Direction.Bottom -> 0 to 1
    }
