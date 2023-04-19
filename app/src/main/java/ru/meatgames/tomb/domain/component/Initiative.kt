package ru.meatgames.tomb.domain.component

enum class Initiative(val value: Int) {
    SuperHigh(5),
    High(4),
    Medium(3),
    Low(2),
    SuperLow(1),
}

enum class InitiativeDiff {
    TwiceAsFast,
    Faster,
    Same,
    Slower,
    TwiceAsSlow,
}

fun Initiative.compareInitiative(
    otherInitiative: Initiative,
): InitiativeDiff {
    val diff = otherInitiative.value - value
    return when {
        diff == 0 -> InitiativeDiff.Same
        diff == 1 -> InitiativeDiff.Faster
        diff > 1 -> InitiativeDiff.TwiceAsFast
        diff == -1 -> InitiativeDiff.Slower
        else -> InitiativeDiff.TwiceAsSlow
    }
}
