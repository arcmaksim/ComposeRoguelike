package ru.meatgames.tomb.model.temp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.meatgames.tomb.model.tile.domain.FloorRenderTile
import ru.meatgames.tomb.model.tile.domain.ObjectRenderTile
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

private const val tileSize = 24

@OptIn(ExperimentalSerializationApi::class)
@Singleton
class ThemeAssets @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val currentTheme: CurrentTheme

    private val wallsThemes: WallsThemes
    private val floorThemes: FloorThemes
    private val doorsThemes: DoorsThemes
    private val stairsThemes: StairsThemes

    init {
        wallsThemes = context.loadWalls()
        floorThemes = context.loadFloor()
        doorsThemes = context.loadDoors()
        stairsThemes = context.loadStairs()

        val theme = wallsThemes.themes.random(Random(System.currentTimeMillis())).title

        currentTheme = CurrentTheme(
            wallsTheme = wallsThemes.themes.first { it.title == theme },
            floorTheme = floorThemes.themes.first { it.title == theme },
            stairsTheme = stairsThemes.themes.first { it.title == theme },
            doorsTheme = doorsThemes.themes.first(),
        )
    }

    private fun Context.getBitmapFromAsset(
        bitmapName: String,
    ): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("images/$bitmapName.png")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun Context.loadWalls(): WallsThemes {
        val atlas = getBitmapFromAsset("walls").asImageBitmap()
        val data = Json.decodeFromStream<WallTilesDto>(
            assets.open("data/walls.json"),
        )

        return WallsThemes(
            atlas = atlas,
            themes = data.themes.map { theme ->
                val verticalOffset = theme.verticalOffset * tileSize
                WallsThemes.Theme(
                    title = theme.name,
                    map = data.tiles.associate {
                        it.tile to IntOffset(tileSize * it.horizontalOffset, verticalOffset)
                    },
                )
            }
        )
    }

    private fun Context.loadFloor(): FloorThemes {
        val atlas = getBitmapFromAsset("floor").asImageBitmap()
        val data = Json.decodeFromStream<FloorTilesDto>(
            assets.open("data/floor.json"),
        )

        return FloorThemes(
            atlas = atlas,
            themes = data.themes.map { theme ->
                val verticalOffset = theme.verticalOffset * tileSize
                FloorThemes.Theme(
                    title = theme.name,
                    map = data.tiles.associate {
                        it.tile to IntOffset(tileSize * it.horizontalOffset, verticalOffset)
                    },
                )
            }
        )
    }

    private fun Context.loadDoors(): DoorsThemes {
        val atlas = getBitmapFromAsset("doors").asImageBitmap()
        val data = Json.decodeFromStream<DoorTilesDto>(
            assets.open("data/doors.json"),
        )

        return DoorsThemes(
            atlas = atlas,
            themes = data.themes.map { theme ->
                val verticalOffset = theme.verticalOffset * tileSize
                DoorsThemes.Theme(
                    title = theme.name,
                    map = data.tiles.associate {
                        it.tile to IntOffset(tileSize * it.horizontalOffset, verticalOffset)
                    },
                )
            }
        )
    }

    private fun Context.loadStairs(): StairsThemes {
        val atlas = getBitmapFromAsset("stairs").asImageBitmap()
        val data = Json.decodeFromStream<StairTilesDto>(
            assets.open("data/stairs.json"),
        )

        return StairsThemes(
            atlas = atlas,
            themes = data.themes.map { theme ->
                val verticalOffset = theme.verticalOffset * tileSize
                StairsThemes.Theme(
                    title = theme.name,
                    map = data.tiles.associate {
                        it.tile to IntOffset(tileSize * it.horizontalOffset, verticalOffset)
                    },
                )
            }
        )
    }

    fun resolveFloorRenderData(
        floorRenderTile: FloorRenderTile,
    ): Pair<ImageBitmap, IntOffset> = when (floorRenderTile) {
        FloorRenderTile.Floor -> {
            floorThemes.atlas to currentTheme.floorTheme.map.getValue(floorRenderTile)
        }
    }

    fun resolveObjectRenderData(
        objectRenderTile: ObjectRenderTile,
    ): Pair<ImageBitmap, IntOffset> = when (objectRenderTile) {
        ObjectRenderTile.DoorClosed,
        ObjectRenderTile.DoorOpened -> {
            doorsThemes.atlas to currentTheme.doorsTheme.map.getValue(objectRenderTile)
        }

        ObjectRenderTile.StairsDown,
        ObjectRenderTile.StairsUp -> {
            stairsThemes.atlas to currentTheme.stairsTheme.map.getValue(objectRenderTile)
        }

        ObjectRenderTile.Wall0,
        ObjectRenderTile.Wall1,
        ObjectRenderTile.Wall2,
        ObjectRenderTile.Wall3,
        ObjectRenderTile.Wall4,
        ObjectRenderTile.Wall5,
        ObjectRenderTile.Wall6,
        ObjectRenderTile.Wall7,
        ObjectRenderTile.Wall8,
        ObjectRenderTile.Wall9,
        ObjectRenderTile.Wall10,
        ObjectRenderTile.Wall11,
        ObjectRenderTile.Wall12,
        ObjectRenderTile.Wall13,
        ObjectRenderTile.Wall14,
        ObjectRenderTile.Wall15 -> {
            wallsThemes.atlas to currentTheme.wallsTheme.map.getValue(objectRenderTile)
        }
    }

}