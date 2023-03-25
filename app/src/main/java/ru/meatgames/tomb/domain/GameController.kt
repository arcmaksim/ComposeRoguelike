package ru.meatgames.tomb.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameController @Inject constructor(
    private val mapCreator: MapCreator,
    private val characterController: CharacterController,
) {

    fun generateNewMap() {
        val configuration = mapCreator.createNewMap()
        characterController.setPosition(
            coordinates = configuration.startCoordinates,
        )
    }

}
