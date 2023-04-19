package ru.meatgames.tomb.render

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import ru.meatgames.tomb.domain.enemy.EnemyId

data class RenderData(
    val asset: ImageBitmap,
    val offset: IntOffset,
)

data class AnimationRenderData(
    val asset: ImageBitmap,
    val offsets: List<IntOffset>,
    val shadowRenderData: RenderData,
    val healthRatio: Float,
    val shadowHorizontalOffset: Int = 0,
    val enemyId: EnemyId? = null,
)
