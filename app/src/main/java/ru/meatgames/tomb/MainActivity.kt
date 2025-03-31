package ru.meatgames.tomb

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.model.IllustrationAssets
import ru.meatgames.tomb.screen.compose.LocalIllustrationAssets
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var gameController: GameController

    @Inject
    lateinit var illustrationAssets: IllustrationAssets

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFullScreenMode()
    
        setContent {
            CompositionLocalProvider(
                LocalIllustrationAssets provides illustrationAssets,
            ) {
                TombApp(
                    viewModel = hiltViewModel(),
                    onCloseApp = ::finish,
                )
            }
        }
    }

    private fun setupFullScreenMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    
}
