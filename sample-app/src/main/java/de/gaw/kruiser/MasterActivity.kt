package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.debug.DebugBackstackLoggerEffect
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.renderer.RenderDestinations
import de.gaw.kruiser.renderer.RenderOverlays
import de.gaw.kruiser.ui.theme.KruiserSampleTheme

class MasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KruiserSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val masterBackstack = masterNavigationStateViewModel().backstack

                    Backstack(
                        state = masterBackstack,
                    ) { backstack ->
                        Box {
                            RenderDestinations(backstack = backstack)
                            RenderOverlays(backstack = backstack)
                        }
                    }

                    DebugBackstackLoggerEffect(
                        tag = "MasterBackstack",
                        backstack = masterBackstack,
                    )
                }
            }
        }
    }
}