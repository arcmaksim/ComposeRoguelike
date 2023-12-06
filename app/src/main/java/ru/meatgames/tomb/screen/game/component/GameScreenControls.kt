package ru.meatgames.tomb.screen.game.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggles
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.domain.item.ItemContainerId
import ru.meatgames.tomb.domain.item.ItemId
import ru.meatgames.tomb.screen.game.GameScreenInteractionController
import ru.meatgames.tomb.screen.game.animation.EnemyAnimationEvent
import ru.meatgames.tomb.toDirection

@Preview
@Composable
private fun GameScreenControlsPreview() {
    GameScreenControls(
        modifier = Modifier.size(300.dp),
        interactionController = object : GameScreenInteractionController {
            override fun finishPlayerAnimation() = Unit
            override fun processCharacterMoveInput(direction: Direction) = Unit
            override fun closeInteractionMenu() = Unit
            override fun itemSelected(itemContainerId: ItemContainerId, itemId: ItemId) = Unit
            override fun finishEnemiesAnimation() = Unit
            override fun skipTurn() = Unit
            override fun onEnemyAnimationEvent(event: EnemyAnimationEvent) = Unit
        },
    )
}

@Composable
fun GameScreenControls(
    interactionController: GameScreenInteractionController,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val shouldDrawMovementControls = FeatureToggles.getToggleValue(FeatureToggle.ShowMovementControls)
    
    BoxWithConstraints(
        modifier = modifier.then(
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            val width = with(density) { size.width.toDp() }
                            (interactionController::processCharacterMoveInput)(it.toDirection(width))
                        },
                    )
                }
                .drawWithCache {
                    onDrawBehind {
                        if (shouldDrawMovementControls) drawBounds()
                    }
                },
        )
    ) {
        if (shouldDrawMovementControls) BoundsLabels()
    }
}

private fun DrawScope.drawBounds() {
    val size = size.width
    drawLine(
        color = Color.Red,
        start = Offset.Zero,
        end = Offset(size, 0f),
        strokeWidth = 4F,
    )
    drawLine(
        color = Color.Red,
        start = Offset.Zero,
        end = Offset(size, size),
        strokeWidth = 4F,
    )
    drawLine(
        color = Color.Red,
        start = Offset(size, 0f),
        end = Offset(0f, size),
        strokeWidth = 4F,
    )
    drawLine(
        color = Color.Red,
        start = Offset(0f, size),
        end = Offset(size, size),
        strokeWidth = 4F,
    )
}

@Composable
private fun BoxWithConstraintsScope.BoundsLabels() {
    val style = remember {
        h2TextStyle.copy(
            shadow = Shadow(
                color = Color.White,
                offset = Offset.Zero,
                blurRadius = 16f,
            ),
        )
    }
    Text(
        modifier = Modifier
            .padding(bottom = maxWidth * .66f)
            .align(Alignment.Center),
        text = "Up",
        style = style,
        color = Color.Red,
    )
    Text(
        modifier = Modifier
            .padding(top = maxWidth * .66f)
            .align(Alignment.Center),
        text = "Down",
        style = style,
        color = Color.Red,
    )
    Text(
        modifier = Modifier
            .padding(end = maxWidth * .66f)
            .align(Alignment.Center),
        text = "Left",
        style = style,
        color = Color.Red,
    )
    Text(
        modifier = Modifier
            .padding(start = maxWidth * .66f)
            .align(Alignment.Center),
        text = "Right",
        style = style,
        color = Color.Red,
    )
}
