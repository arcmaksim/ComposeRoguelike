package ru.meatgames.tomb.domain.map

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.di.MECHANICS_PLAYGROUND_MAP_GENERATOR
import ru.meatgames.tomb.di.MAIN_MAP_GENERATOR
import ru.meatgames.tomb.di.MAP_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_WIDTH_KEY
import ru.meatgames.tomb.di.TESTING_PLAYGROUND_MAP_GENERATOR
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.enemy.EnemiesHolder
import ru.meatgames.tomb.domain.item.ItemsHolder
import ru.meatgames.tomb.domain.mapgenerator.MapConfiguration
import ru.meatgames.tomb.domain.mapgenerator.MapGenerator
import ru.meatgames.tomb.model.tile.domain.ObjectEntityTile
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapControllerImpl @Inject constructor(
    @Named(MAP_WIDTH_KEY) private val mapWidth: Int,
    @Named(MAP_HEIGHT_KEY) private val mapHeight: Int,
    @Named(MAIN_MAP_GENERATOR) private val mainMapGenerator: MapGenerator,
    @Named(MECHANICS_PLAYGROUND_MAP_GENERATOR) private val mechanicsPlaygroundMapGenerator: MapGenerator,
    @Named(TESTING_PLAYGROUND_MAP_GENERATOR) private val playgroundMapGenerator: MapGenerator,
    private val itemsHolder: ItemsHolder,
    private val enemiesHolder: EnemiesHolder,
) : MapCreator, MapTerraformer, MapController {

    private lateinit var levelMap: LevelMap

    private val _mapFlow: MutableStateFlow<MapState> = MutableStateFlow(MapState.MapUnavailable)
    override val mapFlow: StateFlow<MapState> = _mapFlow

    override fun createNewMap(
        type: MapCreator.MapType,
    ): MapConfiguration {
        _mapFlow.value = MapState.MapUnavailable
    
        val levelMap = LevelMap(mapWidth, mapHeight).also { levelMap = it }
        itemsHolder.clearContainers()
        enemiesHolder.clearEnemies()
        FeatureToggles.themeOverride = null

        val configuration = when (type) {
            MapCreator.MapType.MAIN -> mainMapGenerator.generateMap(levelMap)
            MapCreator.MapType.MECHANICS_PLAYGROUND -> mechanicsPlaygroundMapGenerator.generateMap(levelMap)
            MapCreator.MapType.TESTING_PLAYGROUND -> playgroundMapGenerator.generateMap(levelMap)
        }

        _mapFlow.value = MapState.MapAvailable(levelMap)

        return configuration
    }

    override fun getTile(
        coordinates: Coordinates,
    ): MapTile? = levelMap.getTile(coordinates)

    override fun changeObject(
        x: Int,
        y: Int,
        objectEntityTile: ObjectEntityTile?,
    ) {
        levelMap.updateSingleTile(
            x = x,
            y = y,
            update = {
                copy(
                    objectEntityTile = objectEntityTile,
                )
            },
        )
    }
}

interface MapCreator {
    fun createNewMap(
        type: MapType,
    ): MapConfiguration
    
    enum class MapType {
        MAIN,
        MECHANICS_PLAYGROUND,
        TESTING_PLAYGROUND,
    }
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
    ): MapTile?
}

sealed class MapState {

    data class MapAvailable(
        val levelMap: LevelMap,
    ) : MapState()

    object MapUnavailable : MapState()

}
