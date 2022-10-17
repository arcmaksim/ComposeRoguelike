package ru.meatgames.tomb.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

private const val MAP_WIDTH = 32
const val MAP_WIDTH_KEY = "MAP_WIDTH"
private const val MAP_HEIGHT = 32
const val MAP_HEIGHT_KEY = "MAP_HEIGHT"

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Named(MAP_WIDTH_KEY)
    @Provides
    fun mapWidthConst(): Int = MAP_WIDTH

    @Named(MAP_HEIGHT_KEY)
    @Provides
    fun mapHeightConst(): Int = MAP_HEIGHT

}
