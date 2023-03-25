package ru.meatgames.tomb.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.di.MAP_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_WIDTH_KEY
import ru.meatgames.tomb.domain.mapgenerator.MainMapGenerator
import ru.meatgames.tomb.domain.mapgenerator.MapConfiguration
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapControllerImpl @Inject constructor(
    @Named(MAP_WIDTH_KEY) private val mapWidth: Int,
    @Named(MAP_HEIGHT_KEY) private val mapHeight: Int,
    private val mapGenerator: MainMapGenerator,
    private val itemsHolder: ItemsHolder,
) : MapCreator, MapTerraformer, MapController {

    private lateinit var levelMap: LevelMap

    private val _mapFlow: MutableStateFlow<MapState> = MutableStateFlow(MapState.MapUnavailable)
    override val mapFlow: StateFlow<MapState> = _mapFlow

    override fun createNewMap(): MapConfiguration {
        _mapFlow.value = MapState.MapUnavailable
    
        itemsHolder.clearContainers()
        val levelMap = LevelMap(mapWidth, mapHeight).also { levelMap = it }
        val configuration = mapGenerator.generateMap(levelMap)

        _mapFlow.value = MapState.MapAvailable(
            LevelMapWrapper(
                width = configuration.mapWidth,
                height = configuration.mapHeight,
                state = levelMap.state,
            )
        )

        return configuration
    }

    override fun getTile(
        coordinates: Coordinates,
    ): MapTileWrapper? = levelMap.getTile(coordinates.first, coordinates.second)

    override fun changeObject(
        x: Int,
        y: Int,
        objectEntityTile: ObjectEntityTile?,
    ) {
        levelMap.updateSingleTile(
            x = x,
            y = y,
        ) {
            copy(
                objectEntityTile = objectEntityTile,
            )
        }
    }
}

interface MapCreator {
    fun createNewMap(): MapConfiguration
}

interface MapTerraformer {
    fun changeObject(
        x: Int,
        y: Int,
        objectEntityTile: ObjectEntityTile?,
    )
}

interface MapController {
    val mapFlow: StateFlow<MapState>

    fun getTile(
        coordinates: Coordinates,
    ): MapTileWrapper?
}

data class LevelMapWrapper(
    val width: Int,
    val height: Int,
    val state: StateFlow<List<MapTile>>,
) {

    override fun toString(): String {
        val flowValue = state.value
        return flowValue.mapIndexed { index, value ->
            val nextLinePostfix = if (index % width == width - 1) "\n" else ""
            if (value.objectEntityTile == null) ".$nextLinePostfix" else "#$nextLinePostfix"
        }.fold("") { acc, item -> acc + item }
    }

}

sealed class MapState {

    data class MapAvailable(
        val mapWrapper: LevelMapWrapper,
    ) : MapState()

    object MapUnavailable : MapState()

}
