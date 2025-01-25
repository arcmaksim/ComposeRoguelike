package ru.meatgames.tomb.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.serialization.ExperimentalSerializationApi
import ru.meatgames.tomb.render.Illustration
import ru.meatgames.tomb.render.RenderData
import javax.inject.Inject
import javax.inject.Singleton

private const val ILLUSTRATION_DIMENSION = 16

@OptIn(ExperimentalSerializationApi::class)
@Singleton
class IllustrationAssets @Inject constructor(
    private val illustrationAtlas: ImageBitmap,
) {

    fun getRenderData(
        illustration: Illustration,
    ): RenderData = when (illustration) {
        Illustration.Clock -> illustrationAtlas.toIconRenderData(
            iconAtlasX = 12,
            iconAtlasY = 7,
        )

        Illustration.Bag -> illustrationAtlas.toIconRenderData(
            iconAtlasX = 6,
            iconAtlasY = 4,
        )

        Illustration.Heart -> illustrationAtlas.toIconRenderData(
            iconAtlasX = 19,
            iconAtlasY = 4,
        )

        Illustration.Cloak -> illustrationAtlas.toIconRenderData(
            iconAtlasX = 8,
            iconAtlasY = 12,
        )
    }

    private fun ImageBitmap.toIconRenderData(
        iconAtlasX: Int,
        iconAtlasY: Int,
        sizeModifier: Float = 1f,
    ): RenderData = RenderData(
        asset = this,
        offset = IntOffset(iconAtlasX * ILLUSTRATION_DIMENSION, iconAtlasY * ILLUSTRATION_DIMENSION),
        size = IntSize(ILLUSTRATION_DIMENSION, ILLUSTRATION_DIMENSION),
        sizeModifier = sizeModifier,
    )

}
