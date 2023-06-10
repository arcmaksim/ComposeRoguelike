package ru.meatgames.tomb.screen.compose.featuretoggle

import Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.meatgames.tomb.R
import ru.meatgames.tomb.config.FeatureToggle
import ru.meatgames.tomb.config.FeatureToggleState
import ru.meatgames.tomb.design.h3TextStyle

@Preview
@Composable
fun FeatureToggleScreenPreview() {
    FeatureToggleScreenContent(
        featureToggles = emptyList(),
        onFeatureToggleUpdate = { _, _ -> Unit },
        onBack = { Unit },
    )
}

@Composable
fun FeatureToggleScreen(
    viewModel: FeatureToggleScreenVM = hiltViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                FeatureToggleScreenEvent.Back -> onBack()
                else -> Unit
            }
        }
    }
    
    val featureToggles by viewModel.state.collectAsStateWithLifecycle()
    
    FeatureToggleScreenContent(
        featureToggles = featureToggles,
        onFeatureToggleUpdate = viewModel::updateToggle,
        onBack = viewModel::navigateBack,
    )
}

@Composable
private fun FeatureToggleScreenContent(
    featureToggles: List<FeatureToggleState>,
    onFeatureToggleUpdate: (FeatureToggle, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Toolbar(
            title = "Feature toggles",
            navigationIconResId = R.drawable.ic_arrow_back,
            onNavigationIcon = onBack,
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            items(featureToggles) { featureToggle ->
                FeatureToggle(
                    featureToggle = featureToggle,
                    onFeatureToggleUpdate = onFeatureToggleUpdate,
                )
            }
        }
    }
}

@Composable
private fun FeatureToggle(
    featureToggle: FeatureToggleState,
    onFeatureToggleUpdate: (FeatureToggle, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = featureToggle.title,
            style = h3TextStyle,
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Switch(
            checked = featureToggle.value,
            onCheckedChange = { onFeatureToggleUpdate(featureToggle.key, it) },
        )
    }
}
