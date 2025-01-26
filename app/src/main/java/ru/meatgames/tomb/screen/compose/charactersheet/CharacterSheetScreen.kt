package ru.meatgames.tomb.screen.compose.charactersheet

import Toolbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.R
import ru.meatgames.tomb.model.AssetsLoader
import ru.meatgames.tomb.render.Illustration
import ru.meatgames.tomb.screen.compose.LocalIllustrationAssets

@Preview(widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun InventoryScreenPreview() {
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalIllustrationAssets provides AssetsLoader(context).illustrationAssets,
    ) {
        CharacterSheetScreenContent(
            state = characterSheetStatePreview,
            onBack = { Unit },
        )
    }
}

@Composable
fun CharacterSheetScreen(
    viewModel: CharacterSheetVM = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterSheetScreenContent(
        state = state,
        onBack = viewModel::onBack,
    )
}

@Composable
private fun CharacterSheetScreenContent(
    state: CharacterSheetState,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Toolbar(
            title = "Character Sheet",
            navigationIconResId = R.drawable.ic_arrow_back,
            onNavigationIcon = onBack,
        )

        val scrollableState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollableState)
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DepletableStat(
                title = "Health",
                illustration = Illustration.Heart,
                color = Color.Red,
                currentValue = "${state.health.currentHealth}",
                maxValue = "${state.health.maxHealth}",
            )
            RangeStat(
                title = "Damage",
                illustration = Illustration.Sword,
                minValue = "2",
                maxValue = "2",
            )
            SingleStat(
                title = "Defense",
                illustration = Illustration.Shield,
                value = "0",
            )
        }
    }
}
