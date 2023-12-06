package ru.meatgames.tomb.screen.charactersheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.cunningColor
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.design.h3TextStyle
import ru.meatgames.tomb.design.powerColor
import ru.meatgames.tomb.design.speedColor
import ru.meatgames.tomb.design.techniqueColor
import ru.meatgames.tomb.domain.behaviorcard.BehaviorCard

@Preview
@Composable
private fun InventoryScreenPreview() {
    BehaviorCards(
        offenseBehaviorCard = mightBehaviorCardPreview,
        defenceBehaviorCard = resilienceBehaviorCardPreview,
        supportBehaviorCard = alertnessBehaviorCardPreview,
    )
}

@Composable
internal fun BehaviorCards(
    offenseBehaviorCard: BehaviorCard?,
    defenceBehaviorCard: BehaviorCard?,
    supportBehaviorCard: BehaviorCard?,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            GroupTitle("Offensive")
            BehaviorCardSlot {
                offenseBehaviorCard?.let { BehaviorCard(it) } ?: ContentText("None")
            }
    
            Spacer(modifier = Modifier.height(24.dp))
            
            GroupTitle("Defensive")
            BehaviorCardSlot {
                defenceBehaviorCard?.let { BehaviorCard(it) } ?: ContentText("None")
            }
    
            Spacer(modifier = Modifier.height(24.dp))
            
            GroupTitle("Support")
            BehaviorCardSlot {
                supportBehaviorCard?.let { BehaviorCard(it) } ?: ContentText("None")
            }
        }
    }
}

@Composable
private fun GroupTitle(
    title: String,
) {
    Text(
        text = title,
        style = h2TextStyle,
    )
}

@Composable
private fun ContentText(
    title: String,
    highlightColor: Color? = null,
) {
    val modifier = highlightColor?.let {
        Modifier.background(
            color = it,
            shape = RoundedCornerShape(8.dp),
        )
    } ?: Modifier
    
    val textColor = highlightColor?.let {
        if (it.luminance() < .5f) Color.White else Color.Black
    } ?: Color.White
    
    Box(
        modifier = modifier.padding(4.dp),
    ) {
        Text(
            text = title,
            style = h3TextStyle,
            color = textColor,
        )
    }
}

@Composable
private fun BehaviorCardSlot(
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(12.dp),
        contentAlignment = Alignment.TopStart,
        content = content,
    )
}

@Composable
private fun BehaviorCard(
    behaviorCard: BehaviorCard,
) {
    Column {
        ContentText(behaviorCard.title)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        behaviorCard.power?.let {
            ContentText(
                title = "+${it.value} Power",
                highlightColor = powerColor,
            )
        }
        behaviorCard.speed?.let {
            ContentText(
                title = "+${it.value} Speed",
                highlightColor = speedColor,
            )
        }
        behaviorCard.cunning?.let {
            ContentText(
                title = "+${it.value} Cunning",
                highlightColor = cunningColor,
            )
        }
        behaviorCard.technique?.let {
            ContentText(
                title = "+${it.value} Technique",
                highlightColor = techniqueColor,
            )
        }
    }
}
