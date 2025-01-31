package ru.meatgames.tomb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.meatgames.tomb.ScenesNavigator.Command
import javax.inject.Inject
import javax.inject.Singleton

interface ScenesNavigator {

    val commandFlow: Flow<Command>

    fun navigateTo(
        command: Command,
    )

    sealed class Command {
        data object NavigateBack : Command()

        data class NavigateTo(
            val scene: Scene,
            val popUpToRoot: Boolean = false,
        ) : Command()
    }

}

@Singleton
class ScenesNavigatorImpl @Inject constructor() : ScenesNavigator {

    private val _commandFlow = MutableSharedFlow<Command>(extraBufferCapacity = 2)
    override val commandFlow: Flow<Command>
        get() = _commandFlow.asSharedFlow()

    override fun navigateTo(
        command: Command,
    ) {
        _commandFlow.tryEmit(command)
    }

}

fun Scene.asNavigationToCommand(
    asTopMost: Boolean = false,
): Command = Command.NavigateTo(
    scene = this,
    popUpToRoot = asTopMost,
)
