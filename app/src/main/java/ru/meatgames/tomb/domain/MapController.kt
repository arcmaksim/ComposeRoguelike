package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.di.MAP_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_WIDTH_KEY
import ru.meatgames.tomb.new_models.themed.domain.tile.ThemedTile
import ru.meatgames.tomb.new_models.themed.domain.tile.isEmpty
import ru.meatgames.tomb.screen.compose.game.ThemedGameMapTile
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapControllerImpl @Inject constructor(
    @Named(MAP_WIDTH_KEY) private val mapWidth: Int,
    @Named(MAP_HEIGHT_KEY) private val mapHeight: Int,
    private val mapGenerator: SimpleMapGenerator,
) : MapGenerator, MapTerraformer, MapController {

    private lateinit var levelMap: LevelMap

    private val _mapFlow: MutableStateFlow<State> = MutableStateFlow(State.MapUnavailable)
    override val mapFlow: StateFlow<State> = _mapFlow

    override fun generateNewMap(): GeneratedMapConfiguration {
        _mapFlow.value = State.MapUnavailable

        val levelMap = LevelMap(mapWidth, mapHeight).also { levelMap = it }
        val configuration = mapGenerator.generateMap(levelMap)

        _mapFlow.value = State.MapAvailable(
            LevelMapWrapper(
                width = configuration.mapWidth,
                height = configuration.mapHeight,
                state = levelMap.state,
            )
        )

        return configuration
    }

    override fun getTile(
        x: Int,
        y: Int,
    ): ThemedGameMapTile? = levelMap.getTile(x, y)

    override fun changeObject(
        x: Int,
        y: Int,
        objectTile: ThemedTile,
    ) {
        levelMap.updateSingleTile(
            x = x,
            y = y,
        ) {
            copy(
                `object` = objectTile,
            )
        }
    }
}

interface MapGenerator {
    fun generateNewMap(): GeneratedMapConfiguration
}

interface MapTerraformer {
    fun changeObject(
        x: Int,
        y: Int,
        objectTile: ThemedTile,
    )
}

interface MapController {
    val mapFlow: StateFlow<State>

    fun getTile(
        x: Int,
        y: Int,
    ): ThemedGameMapTile?
}

data class LevelMapWrapper(
    val width: Int,
    val height: Int,
    val state: StateFlow<List<ThemedGameMapTile>>,
) {

    override fun toString(): String {
        val flowValue = state.value
        return flowValue.mapIndexed { index, value ->
            val nextLinePostfix = if (index % width == width - 1) "\n" else ""
            if (value.`object`?.purposeDefinition?.isEmpty == true) ".$nextLinePostfix" else "#$nextLinePostfix"
        }.fold("") { acc, item -> acc + item }
    }

}

sealed class State {

    data class MapAvailable(
        val mapWrapper: LevelMapWrapper,
    ) : State()

    object MapUnavailable : State()

}