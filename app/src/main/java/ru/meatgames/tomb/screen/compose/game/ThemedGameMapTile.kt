package ru.meatgames.tomb.screen.compose.game

import ru.meatgames.tomb.new_models.tile.domain.ThemedTile

data class ThemedGameMapTile(
    val floor: ThemedTile? = null,
    val `object`: ThemedTile? = null,
) {

    companion object {
        val voidMapTile = ThemedGameMapTile()
    }

    val isPassable: Boolean
        get() = floor?.isPassable ?: false && `object`?.isPassable ?: true

}
