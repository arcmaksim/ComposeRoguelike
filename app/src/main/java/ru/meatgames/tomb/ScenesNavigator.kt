package ru.meatgames.tomb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface ScenesNavigator {

    val scene: StateFlow<SceneNavigationCommand>

    fun navigateTo(
        navigationCommand: SceneNavigationCommand,
    )

}

@Singleton
class ScenesNavigatorImpl @Inject constructor() : ScenesNavigator {

    private val _scene = MutableStateFlow<SceneNavigationCommand>(
        SceneNavigationCommand(Scene.MainMenu)
    )
    override val scene: StateFlow<SceneNavigationCommand>
        get() = _scene.asStateFlow()

    override fun navigateTo(
        navigationCommand: SceneNavigationCommand,
    ) {
        _scene.value = navigationCommand
    }

}

data class SceneNavigationCommand(
    val scene: Scene,
    val popUpToTop: Boolean = false,
)

fun Scene.toSceneNavigationCommand(): SceneNavigationCommand = SceneNavigationCommand(this)
