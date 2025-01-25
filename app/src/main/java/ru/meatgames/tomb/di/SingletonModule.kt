package ru.meatgames.tomb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.meatgames.tomb.ScenesNavigator
import ru.meatgames.tomb.ScenesNavigatorImpl
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
import ru.meatgames.tomb.domain.mapgenerator.MechanicsPlaygroundMapGenerator
import ru.meatgames.tomb.domain.mapgenerator.MainMapGenerator
import ru.meatgames.tomb.domain.mapgenerator.MapGenerator
import ru.meatgames.tomb.domain.mapgenerator.PlaygroundMapGenerator
import ru.meatgames.tomb.model.AssetsLoader
import ru.meatgames.tomb.model.IllustrationAssets
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
const val MECHANICS_PLAYGROUND_MAP_GENERATOR = "MECHANICS_PLAYGROUND_MAP_GENERATOR"
const val TESTING_PLAYGROUND_MAP_GENERATOR = "TESTING_PLAYGROUND_MAP_GENERATOR"

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {

    @Binds
    fun mapCreator(
        impl: MapControllerImpl,
    ): MapCreator

    @Binds
    fun mapTerraformer(
        impl: MapControllerImpl,
    ): MapTerraformer

    @Binds
    fun mapController(
        impl: MapControllerImpl,
    ): MapController
    
    @Binds
    fun itemsHolder(
        impl: ItemsControllerImpl,
    ): ItemsHolder
    
    @Binds
    fun itemsController(
        impl: ItemsControllerImpl,
    ): ItemsController
    
    @Binds
    fun enemiesHolder(
        impl: EnemiesControllerImpl,
    ): EnemiesHolder
    
    @Binds
    fun enemiesController(
        impl: EnemiesControllerImpl,
    ): EnemiesController

    @Binds
    @IntoSet
    fun wallsDecorator(
        impl: WallsDecorator,
    ): MapRenderTilesDecorator
    
    @Binds
    @Named(MAIN_MAP_GENERATOR)
    fun mainMapGenerator(
        impl: MainMapGenerator,
    ): MapGenerator

    @Binds
    @Named(MECHANICS_PLAYGROUND_MAP_GENERATOR)
    fun mechanicsPlaygroundMapGenerator(
        impl: MechanicsPlaygroundMapGenerator,
    ): MapGenerator
    
    @Binds
    @Named(TESTING_PLAYGROUND_MAP_GENERATOR)
    fun playgroundMapGenerator(
        impl: PlaygroundMapGenerator,
    ): MapGenerator
    
    @Binds
    fun gameController(
        impl: GameControllerImpl,
    ): GameController

    @Binds
    fun scenesNavigator(
        impl: ScenesNavigatorImpl,
    ): ScenesNavigator

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

        @Provides
        fun illustrationAssets(
            assetsLoader: AssetsLoader,
        ): IllustrationAssets = assetsLoader.illustrationAssets
    }
    
}
