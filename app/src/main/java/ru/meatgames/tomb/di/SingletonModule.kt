package ru.meatgames.tomb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.meatgames.tomb.domain.enemy.EnemiesController
import ru.meatgames.tomb.domain.enemy.EnemiesControllerImpl
import ru.meatgames.tomb.domain.enemy.EnemiesHolder
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.GameControllerImpl
import ru.meatgames.tomb.domain.item.ItemsController
import ru.meatgames.tomb.domain.item.ItemsControllerImpl
import ru.meatgames.tomb.domain.item.ItemsHolder
import ru.meatgames.tomb.domain.map.MapController
import ru.meatgames.tomb.domain.map.MapControllerImpl
import ru.meatgames.tomb.domain.map.MapCreator
import ru.meatgames.tomb.domain.map.MapTerraformer
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
    fun MapControllerImpl.bindMapCreator(): MapCreator

    @Binds
    fun MapControllerImpl.bindMapTerraformer(): MapTerraformer

    @Binds
    fun MapControllerImpl.bindMapController(): MapController
    
    @Binds
    fun ItemsControllerImpl.bindItemsHolder(): ItemsHolder
    
    @Binds
    fun ItemsControllerImpl.bindItemsController(): ItemsController
    
    @Binds
    fun EnemiesControllerImpl.bindEnemiesHolder(): EnemiesHolder
    
    @Binds
    fun EnemiesControllerImpl.bindEnemiesController(): EnemiesController

    @Binds
    @IntoSet
    fun WallsDecorator.bindWallsDecorator(): MapRenderTilesDecorator
    
    @Binds
    @Named(MAIN_MAP_GENERATOR)
    fun MainMapGenerator.bindMainMapGenerator(): MapGenerator
    
    @Binds
    @Named(PLAYGROUND_MAP_GENERATOR)
    fun PlaygroundMapGenerator.bindPlaygroundMapGenerator(): MapGenerator
    
    @Binds
    fun GameControllerImpl.bindGameController(): GameController

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
