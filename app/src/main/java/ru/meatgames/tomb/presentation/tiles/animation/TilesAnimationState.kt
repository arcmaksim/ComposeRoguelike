package ru.meatgames.tomb.presentation.tiles.animation

import ru.meatgames.tomb.domain.ScreenSpaceCoordinates

/**
 * @param creationTime is needed to always produce new hash code
 * for each new animation of the same type,
 * and have automatic hash code generation out of the box.
 */

data class TilesAnimationState(
    val creationTime: Long,
    val tilesToFadeIn: Set<ScreenSpaceCoordinates> = emptySet(),
    val tilesToFadeOut: Set<ScreenSpaceCoordinates> = emptySet(),
)
