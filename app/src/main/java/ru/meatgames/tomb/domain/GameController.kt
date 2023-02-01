package ru.meatgames.tomb.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameController @Inject constructor(
    private val mapGenerator: MapGenerator,
    private val characterController: CharacterController,
) {

    init {
        generateNewMap()
    }

    fun generateNewMap() {
        val configuration = mapGenerator.generateNewMap()
        characterController.setInitialState(
            mapX = configuration.startingPositionX,
            mapY = configuration.startingPositionY,
        )
    }

}
