package ru.meatgames.tomb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.meatgames.tomb.domain.MapController
import ru.meatgames.tomb.domain.MapControllerImpl
import ru.meatgames.tomb.domain.MapGenerator
import ru.meatgames.tomb.domain.MapTerraformer
import ru.meatgames.tomb.model.room.data.RoomsData
import ru.meatgames.tomb.model.room.data.RoomsRepository
import ru.meatgames.tomb.render.MapRenderTilesDecorator
import ru.meatgames.tomb.render.WallsDecorator
import javax.inject.Named

private const val MAP_WIDTH = 32
const val MAP_WIDTH_KEY = "MAP_WIDTH"
private const val MAP_HEIGHT = 32
const val MAP_HEIGHT_KEY = "MAP_HEIGHT"

private const val MAP_VIEWPORT_WIDTH = 5
const val MAP_VIEWPORT_WIDTH_KEY = "MAP_VIEWPORT_WIDTH"
private const val MAP_VIEWPORT_HEIGHT = 5
const val MAP_VIEWPORT_HEIGHT_KEY = "MAP_VIEWPORT_HEIGHT"

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {

    @Binds
    fun bindMapGenerator(
        controller: MapControllerImpl,
    ): MapGenerator

    @Binds
    fun bindMapTerraformer(
        controller: MapControllerImpl,
    ): MapTerraformer

    @Binds
    fun bindMapController(
        controller: MapControllerImpl,
    ): MapController

    @Binds
    @IntoSet
    fun bindWallsDecorator(
        decorator: WallsDecorator,
    ): MapRenderTilesDecorator

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
