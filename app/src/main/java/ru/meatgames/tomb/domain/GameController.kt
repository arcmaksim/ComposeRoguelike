package ru.meatgames.tomb.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameController @Inject constructor(
    private val mapCreator: MapCreator,
    private val characterController: CharacterController,
) {
    
    lateinit var lastMapType: MapCreator.MapType
        private set

    fun generateNewMap(
        mapType: MapCreator.MapType,
    ) {
        lastMapType = mapType
        val configuration = mapCreator.createNewMap(mapType)
        characterController.setPosition(
            coordinates = configuration.startCoordinates,
        )
    }

}
