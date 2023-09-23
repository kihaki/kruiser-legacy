package de.gaw.kruiser.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.android.navigationStateOwnerViewModel
import de.gaw.kruiser.sample.samples.DashboardDestination
import de.gaw.kruiser.sample.theme.KruiserTheme
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.push
import de.gaw.kruiser.ui.doublerailstack.AnimatedDoubleRailStack
import de.gaw.kruiser.ui.singletopstack.ScreenStack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("StateRestoring", "Bundle: $savedInstanceState")
        setContent {
            KruiserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationViewModel = navigationStateOwnerViewModel().apply {
                        if (state.currentStack.isEmpty()) state.push(DashboardDestination)
                    }
                    CompositionLocalProvider(
                        LocalScopedServiceProvider provides navigationViewModel.serviceProvider,
                        LocalNavigationState provides navigationViewModel.state,
                    ) {
                        BoxWithConstraints {
                            val isPortrait = maxWidth <= maxHeight
                            when {
                                isPortrait -> ScreenStack(
                                    modifier = Modifier.fillMaxSize(),
                                )

                                else -> AnimatedDoubleRailStack(
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}