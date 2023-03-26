package ru.meatgames.tomb

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import ru.meatgames.tomb.domain.enemy.EnemyType
import ru.meatgames.tomb.screen.compose.game.component.CharacterData
import java.io.IOException
import java.io.InputStream

private const val originalTileSize: Int = 24

enum class ShadowSize {
    Small,
    Medium,
    Big,
}

object NewAssets {

    private lateinit var heroBitmaps: Array<ImageBitmap>
    private lateinit var shadowsBitmaps: Array<ImageBitmap>
    private lateinit var enemiesBitmaps: Array<ImageBitmap>

    val tileSize: IntSize = IntSize(24, 24)

    fun loadAssets(
        context: Context,
    ) {
        val characterAnimationSheet = context.getBitmapFromAsset("character_animation_sheet")
        heroBitmaps = Array(4) { i ->
            Bitmap.createBitmap(
                characterAnimationSheet,
                i * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
            ).asImageBitmap()
        }
        
        val shadows = context.getBitmapFromAsset("shadows")
        shadowsBitmaps = Array(3) { i ->
            Bitmap.createBitmap(
                shadows,
                // ignoring first 3 "flying" shadows for now
                (i + 3) * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
            ).asImageBitmap()
        }
    
        val enemies = context.getBitmapFromAsset("enemies")
        enemiesBitmaps = Array(8) { i ->
            Bitmap.createBitmap(
                enemies,
                i * originalTileSize,
                0,
                originalTileSize,
                originalTileSize,
            ).asImageBitmap()
        }
    }
    
    fun getCharacterBitmap(
        characterData: CharacterData,
        animationFrame: Int,
    ): ImageBitmap = when (characterData) {
        is CharacterData.Player -> getHeroBitmap(animationFrame)
        is CharacterData.Enemy -> getEnemyBitmap(characterData.enemyType, animationFrame)
    }

    fun getHeroBitmap(
        frame: Int,
    ): ImageBitmap = heroBitmaps[frame]
    
    fun getShadow(
        size: ShadowSize,
    ) = when (size) {
        ShadowSize.Small -> shadowsBitmaps[0]
        ShadowSize.Medium -> shadowsBitmaps[1]
        ShadowSize.Big -> shadowsBitmaps[2]
    }
    
    // TODO: enemy frames alternate between two frames, initial frame means first, otherwise - second
    fun getEnemyBitmap(
        enemyType: EnemyType,
        animationFrame: Int,
    ): ImageBitmap {
        val index = when (enemyType) {
            EnemyType.Skeleton -> 0
            EnemyType.SkeletonArcher -> 2
            EnemyType.SkeletonWarrior -> 4
            EnemyType.SkeletonNecromancer -> 6
        }
        return enemiesBitmaps[index + animationFrame]
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
    
    fun Int.getOriginalTileSinglePixelOffset() = this / originalTileSize

}
