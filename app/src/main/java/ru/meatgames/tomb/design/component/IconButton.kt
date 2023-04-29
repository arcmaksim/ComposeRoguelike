package ru.meatgames.tomb.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    @DrawableRes iconResId: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.White,
    onClick: () -> Unit = { Unit },
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier.then(
            Modifier
                .size(64.dp)
                .background(
                    color = Color.DarkGray,
                    shape = shape,
                )
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    enabled = enabled,
                    onClick = onClick,
                    role = Role.Button,
                )
        ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(36.dp),
            painter = painterResource(iconResId),
            contentDescription = "None",
            tint = tint,
        )
    }
}
