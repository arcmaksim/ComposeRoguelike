package ru.meatgames.tomb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.meatgames.tomb.domain.MapController
import ru.meatgames.tomb.domain.MapControllerImpl
import ru.meatgames.tomb.domain.MapGenerator
import ru.meatgames.tomb.domain.MapTerraformer
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.data.RoomsRepository
import javax.inject.Named

private const val MAP_WIDTH = 32
const val MAP_WIDTH_KEY = "MAP_WIDTH"
private const val MAP_HEIGHT = 32
const val MAP_HEIGHT_KEY = "MAP_HEIGHT"

private const val MAP_VIEWPORT_WIDTH = 11
const val MAP_VIEWPORT_WIDTH_KEY = "MAP_VIEWPORT_WIDTH"
private const val MAP_VIEWPORT_HEIGHT = 11
const val MAP_VIEWPORT_HEIGHT_KEY = "MAP_VIEWPORT_HEIGHT"

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

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

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingSingletonModule {

    @Binds
    abstract fun bindMapGenerator(
        controller: MapControllerImpl,
    ): MapGenerator

    @Binds
    abstract fun bindMapTerraformer(
        controller: MapControllerImpl,
    ): MapTerraformer

    @Binds
    abstract fun bindMapController(
        controller: MapControllerImpl,
    ): MapController

}
