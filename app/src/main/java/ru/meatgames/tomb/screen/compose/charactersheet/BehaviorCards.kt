package ru.meatgames.tomb.screen.compose.charactersheet

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.meatgames.tomb.design.h2TextStyle
import ru.meatgames.tomb.design.h3TextStyle
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
) {
    Text(
        text = title,
        style = h3TextStyle,
    )
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
            ContentText("+${it.value} Power")
        }
        behaviorCard.speed?.let {
            ContentText("+${it.value} Speed")
        }
        behaviorCard.cunning?.let {
            ContentText("+${it.value} Cunning")
        }
        behaviorCard.technique?.let {
            ContentText("+${it.value} Technique")
        }
    }
}
