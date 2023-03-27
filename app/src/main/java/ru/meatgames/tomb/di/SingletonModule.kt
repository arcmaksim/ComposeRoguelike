package ru.meatgames.tomb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.meatgames.tomb.domain.EnemiesController
import ru.meatgames.tomb.domain.EnemiesControllerImpl
import ru.meatgames.tomb.domain.EnemiesHolder
import ru.meatgames.tomb.domain.ItemsController
import ru.meatgames.tomb.domain.ItemsControllerImpl
import ru.meatgames.tomb.domain.ItemsHolder
import ru.meatgames.tomb.domain.MapController
import ru.meatgames.tomb.domain.MapControllerImpl
import ru.meatgames.tomb.domain.MapCreator
import ru.meatgames.tomb.domain.MapTerraformer
import ru.meatgames.tomb.domain.mapgenerator.MainMapGenerator
import ru.meatgames.tomb.domain.mapgenerator.MapGenerator
import ru.meatgames.tomb.domain.mapgenerator.PlaygroundMapGenerator
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.data.RoomsRepository
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import ru.meatgames.tomb.render.WallsDecorator
import javax.inject.Named

private const val MAP_WIDTH = 32
const val MAP_WIDTH_KEY = "MAP_WIDTH"
private const val MAP_HEIGHT = 32
const val MAP_HEIGHT_KEY = "MAP_HEIGHT"

private const val MAP_VIEWPORT_WIDTH = 7
const val MAP_VIEWPORT_WIDTH_KEY = "MAP_VIEWPORT_WIDTH"
private const val MAP_VIEWPORT_HEIGHT = 7
const val MAP_VIEWPORT_HEIGHT_KEY = "MAP_VIEWPORT_HEIGHT"

const val MAIN_MAP_GENERATOR = "MAIN_MAP_GENERATOR"
const val PLAYGROUND_MAP_GENERATOR = "PLAYGROUND_MAP_GENERATOR"

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {

    @Binds
    fun bindMapGenerator(
        controller: MapControllerImpl,
    ): MapCreator

    @Binds
    fun bindMapTerraformer(
        controller: MapControllerImpl,
    ): MapTerraformer

    @Binds
    fun bindMapController(
        controller: MapControllerImpl,
    ): MapController
    
    @Binds
    fun bindItemsHolder(
        itemsHolder: ItemsControllerImpl,
    ): ItemsHolder
    
    @Binds
    fun bindItemsController(
        itemsController: ItemsControllerImpl,
    ): ItemsController
    
    @Binds
    fun bindEnemiesHolder(
        enemiesHolder: EnemiesControllerImpl,
    ): EnemiesHolder
    
    @Binds
    fun bindEnemiesController(
        enemiesController: EnemiesControllerImpl,
    ): EnemiesController

    @Binds
    @IntoSet
    fun bindWallsDecorator(
        decorator: WallsDecorator,
    ): MapRenderTilesDecorator
    
    @Binds
    @Named(MAIN_MAP_GENERATOR)
    fun bindMainMapGenerator(
        mainMapGenerator: MainMapGenerator,
    ): MapGenerator
    
    @Binds
    @Named(PLAYGROUND_MAP_GENERATOR)
    fun bindPlaygroundMapGenerator(
        playgroundMapGenerator: PlaygroundMapGenerator,
    ): MapGenerator

    companion object {
        @Named(MAP_WIDTH_KEY)
        @Provides
        fun mapWidthConst(): Int = MAP_WIDTH
    
        @Named(MAP_HEIGHT_KEY)
        @Provides
        fun mapHeightConst(): Int = MAP_HEIGHT
    
        @Named(MAP_VIEWPORT_WIDTH_KEY)
        @Provides
        fun mapViewportWidthConst(): Int = MAP_VIEWPORT_WIDTH
    
        @Named(MAP_VIEWPORT_HEIGHT_KEY)
        @Provides
        fun mapViewportHeightConst(): Int = MAP_VIEWPORT_HEIGHT
    
        @Provides
        fun provideRoomsData(
            roomsRepository: RoomsRepository,
        ): RoomsData = roomsRepository.loadData()
    }
    
}
