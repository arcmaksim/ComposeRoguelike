package ru.meatgames.tomb.screen.compose.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.toPositionComponent
import ru.meatgames.tomb.domain.enemy.Enemy
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.screen.compose.game.LocalBackgroundColor
import ru.meatgames.tomb.screen.compose.game.LocalHorizontalOffset
import ru.meatgames.tomb.screen.compose.game.LocalTileSize

/**
 * There are bunch of magic numbers just to make preview look nice
 */
@Preview(widthDp = 140, heightDp = 140)
@Composable
private fun GameScreenCharacterPreview() {
    val context = LocalContext.current
    
    val themeAssets = ThemeAssets(context)
    val enemyType = Enemy(
        type = EnemyType.SkeletonWarrior,
        position = Coordinates(0, 0).toPositionComponent(),
        health = HealthComponent(maxHealth = 12, currentHealth = 10),
    )
    
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F)
    
    CompositionLocalProvider(
        LocalTileSize provides IntSize(240, 240),
        LocalHorizontalOffset provides IntOffset.Zero,
        LocalBackgroundColor provides Color(0xFF212121),
    ) {
        GameScreenCharacter(
            modifier = modifier.offset(24.dp, 40.dp),
            characterRenderData = themeAssets.getEnemyRenderData(enemyType),
            frameIndex = 0,
            viewportWidth = 1,
            viewportHeight = 1,
        )
    }
}

sealed class CharacterData {

    object Player : CharacterData()
    
    data class Enemy(
        val enemyType: EnemyType,
    ) : CharacterData()

}

@Composable
internal fun GameScreenCharacter(
    modifier: Modifier,
    characterRenderData: AnimationRenderData,
    frameIndex: Int,
    viewportWidth: Int,
    viewportHeight: Int,
) {
    val tileSize = LocalTileSize.current
    val offset = LocalHorizontalOffset.current
    val tileDimension = LocalTileSize.current.width
    
    Canvas(modifier = modifier) {
        drawCharacter(
            tileDimension = tileDimension,
            shadowRenderData = characterRenderData.shadowRenderData,
            frameIndex = frameIndex,
            characterRenderData = characterRenderData,
            dstSize = tileSize,
            dstOffset = offset + IntOffset(
                x = tileDimension * (viewportWidth / 2),
                y = tileDimension * (viewportHeight / 2),
            ),
            alpha = 1f,
        )
    }
}

